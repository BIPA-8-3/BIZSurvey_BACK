package com.bipa.bizsurvey.infra.s3;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Service
@Log4j2
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;
    private final TransferManager transferManager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile, S3Domain domain) {
        String originName = multipartFile.getOriginalFilename();
        StringBuilder path = new StringBuilder();
        S3Folder s3Folder = S3Folder.FILE;

        if (isImageFile(originName)) {
            if (domain != S3Domain.SURVEY) {
                path.append("temp/");
            }
            s3Folder = S3Folder.IMAGES;
        }
        path.append(domain.getDomainName()).append(s3Folder.getFolderName());
        return uploadToS3(multipartFile, path.toString());
    }

    public String uploadSurveyFile(UploadSurveyFileDto dto) {
        MultipartFile multipartFile = dto.getFile();
        String originName = multipartFile.getOriginalFilename();
        StringBuilder path = new StringBuilder(dto.getPath());
        S3Folder s3Folder = S3Folder.FILE;

        if (isImageFile(originName)) {
            s3Folder = S3Folder.IMAGES;
        }

        path.append(s3Folder.getFolderName());
        return uploadToS3(multipartFile, path.toString());
    }

    private String uploadToS3(MultipartFile multipartFile, String path) {
        String originName = multipartFile.getOriginalFilename();
        String fileName = createFilename(path, originName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
        } catch (IOException e) {
            log.error("파일 업로드에 실패했습니다.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");

        }

        return amazonS3Client.getUrl(bucket, fileName).toString().split("//")[1];
    }

    public ResponseEntity<byte[]> downloadFile(String fileUrl) throws IOException {
        fileUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
        String s3FileName = getS3FileName(fileUrl);
        validateFileExists(s3FileName);

        int uuidIndex = s3FileName.indexOf("_") + 1;
        String originName = s3FileName.substring(uuidIndex);

        S3Object s3Object = amazonS3Client.getObject(bucket, s3FileName);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(getContentType(getFileExtension(s3FileName)));
        httpHeaders.setContentDispositionFormData("attachment", new String(originName.getBytes("UTF-8"), "ISO-8859-1"));

        String encodedFileName = UriUtils.encode(originName, "UTF-8");
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    public void downloadZip(String prefix, HttpServletResponse response) throws IOException {
        File localDirectory = new File(RandomStringUtils.randomAlphanumeric(6) + "-download.zip");
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            MultipleFileDownload downloadDirectory = transferManager.downloadDirectory(bucket, prefix, localDirectory);

            log.info("[{}] download progressing... start", prefix);
            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            while (!downloadDirectory.isDone()) {
                Thread.sleep(1000);
                TransferProgress progress = downloadDirectory.getProgress();
                double percentTransferred = progress.getPercentTransferred();
                log.info("[{}] {}% download progressing...", prefix, decimalFormat.format(percentTransferred));
            }
            log.info("[{}] download directory from S3 success!", prefix);
            FileUtil.rename(localDirectory);
            FileUtil.moveFilesToTopLevel(localDirectory);
            log.info("compressing to zip file...");
            addFolderToZip(zipOut, localDirectory);
        } catch (Exception e) {
            log.error("ZIP 파일 압축 중 오류 발생", e);
            throw new RuntimeException("ZIP 파일 압축 중 오류 발생");
        } finally {
            FileUtil.remove(localDirectory);
        }
    }
    private void addFolderToZip(ZipOutputStream zipOut, File localDirectory) throws IOException {
        final int INPUT_STREAM_BUFFER_SIZE = 2048;
        Files.walkFileTree(Paths.get(localDirectory.getName()), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isSymbolicLink()) {
                    return FileVisitResult.CONTINUE;
                }

                try (FileInputStream fis = new FileInputStream(file.toFile())) {
                    Path targetFile = Paths.get(localDirectory.getName()).relativize(file);
                    ZipEntry zipEntry = new ZipEntry(targetFile.toString());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[INPUT_STREAM_BUFFER_SIZE];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    zipOut.closeEntry();
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.err.printf("Unable to zip : %s%n%s%n", file, exc);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    public void deleteFile(String fileUrl) {
        String splitStr = ".com/";
        String fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length());
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private MediaType getContentType(String keyName) {
        String type = getFileExtension(keyName);
        switch (type) {
            case ".txt":
                return MediaType.TEXT_PLAIN;
            case ".png":
                return MediaType.IMAGE_PNG;
            case ".jpeg":
                return MediaType.IMAGE_JPEG;
            case ".gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
    private void validateFileExists(String fileName) throws FileNotFoundException {
        if (!amazonS3Client.doesObjectExist(bucket, fileName))
            throw new FileNotFoundException();
    }

    private String createFilename(String path, String fileName) {
        String baseName = fileName.substring(0, fileName.lastIndexOf("."));
        return path.concat(UUID.randomUUID().toString())
                   .concat("_" + baseName)
                   .concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            return extension.equals(".jpg") ? ".jpeg" : extension;
        } catch (StringIndexOutOfBoundsException se) {
            log.error(se.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    private String getS3FileName(String fileUrl) {
        String splitStr = ".com/";
        int startIndex = fileUrl.lastIndexOf(splitStr) + splitStr.length();
        String fileName = fileUrl.substring(startIndex);
        return fileName;
    }

    private boolean isImageFile(String fileName) {
        String fileExtension = getFileExtension(fileName);
        String[] imageExtensions = {".jpeg", ".png", ".gif"};

        for (String extension : imageExtensions) {
            if (extension.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
}


