package com.bipa.bizsurvey.infra.s3;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.UUID;

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
        String path = createFilePath(originName, domain);
        String fileName = createFilename(path, originName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        String splitStr = ".com/";
        String fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length());
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    public ResponseEntity<byte[]> downloadFile(String fileUrl, String originName) throws IOException {
        String fileName = getFileName(fileUrl);
        validateFileExists(fileName);
        String extension = getFileExtension(originName);

        S3Object s3Object = amazonS3Client.getObject(bucket, fileName);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(getContentType(extension));
        httpHeaders.setContentDispositionFormData("attachment", originName);
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    public Resource downloadZip(String prefix) throws IOException, InterruptedException {
        File localDirectory = new File(RandomStringUtils.randomAlphanumeric(6) + "-s3-download");
        ZipFile zipFile = new ZipFile(RandomStringUtils.randomAlphanumeric(6) + "-s3-download.zip");
        try {
            MultipleFileDownload downloadDirectory = transferManager.downloadDirectory(bucket, prefix, localDirectory);

            log.info("[" + prefix + "] download progressing... start");
            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            while (!downloadDirectory.isDone()) {
                Thread.sleep(1000);
                TransferProgress progress = downloadDirectory.getProgress();
                double percentTransferred = progress.getPercentTransferred();
                log.info("[" + prefix + "] " + decimalFormat.format(percentTransferred) + "% download progressing...");
            }
            log.info("[" + prefix + "] download directory from S3 success!");
            log.info("compressing to zip file...");
            zipFile.addFolder(new File(localDirectory.getName() + "/" + prefix));
        } finally {
            FileUtil.remove(localDirectory);
        }
        return new FileSystemResource(zipFile.getFile().getName());
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
        return path.concat(UUID.randomUUID().toString()).concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            return extension.equals(".jpg") ? ".jpeg" : extension;
        } catch (StringIndexOutOfBoundsException se) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    public String getFileName(String fileUrl) {
        String splitStr = ".com/";
        int startIndex = fileUrl.lastIndexOf(splitStr) + splitStr.length();
        String fileName = fileUrl.substring(startIndex);

        return fileName;
    }

    public boolean isImageFile(String fileName) {
        String fileExtension = getFileExtension(fileName);

        String[] imageExtensions = {".jpeg", ".png", ".gif"};

        for (String extension : imageExtensions) {
            if (extension.equalsIgnoreCase(fileExtension)) {
                return true; // 이미지 파일
            }
        }

        return false; // 이미지 파일이 아님
    }

    public String createFilePath(String originName, S3Domain domain) {
        StringBuilder path = new StringBuilder();
        S3Folder s3Folder = S3Folder.FILE;

        if (isImageFile(originName)) {
            if (domain != S3Domain.SURVEY) {
                path.append("temp/");
            }
            s3Folder = S3Folder.IMAGES;
        }
        path.append(domain.getDomainName()).append(s3Folder.getFolderName());
        return path.toString();
    }
}


