package com.bipa.bizsurvey.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.util.IOUtils;
import com.bipa.bizsurvey.global.common.storage.Domain;
import com.bipa.bizsurvey.global.common.storage.StorageService;
import com.bipa.bizsurvey.global.common.storage.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Service
@Log4j2
public class S3StorageServiceImpl implements StorageService {
    private final AmazonS3Client amazonS3Client;
    private final TransferManager transferManager;
    private final String REGEX = ".com/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file, Domain domain, String path) {
        String originName = file.getOriginalFilename();

        if(StringUtils.isEmpty(path)){
            path = "";
        }

        path = createPath(domain, path, originName);
        String saveName = createFileName(path, originName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, saveName, inputStream, metadata));
        } catch (IOException e) {
            log.error("파일 업로드에 실패했습니다.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }

        return amazonS3Client.getUrl(bucket, saveName).toString().split("//")[1];
    }

    @Override
    public byte[] downloadFile(String fileUrl) throws IOException{
        String s3FileName = extractFileName(fileUrl, REGEX);
        validateFileExists(s3FileName);
        String originName = getOriginName(s3FileName);

        S3Object s3Object = amazonS3Client.getObject(bucket, s3FileName);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

        return IOUtils.toByteArray(objectInputStream);
    }

    @Override
    public void downloadZip(ZipOutputStream zipOut, String fileUrl) throws IOException {
        File localDirectory = new File(RandomStringUtils.randomAlphanumeric(6) + "-download.zip");
        String filePath = extractFileName(fileUrl, REGEX);
        try {
            MultipleFileDownload downloadDirectory = transferManager.downloadDirectory(bucket, filePath, localDirectory);

            log.info("[{}] download progressing... start", filePath);
            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            while (!downloadDirectory.isDone()) {
                Thread.sleep(1000);
                TransferProgress progress = downloadDirectory.getProgress();
                double percentTransferred = progress.getPercentTransferred();
                log.info("[{}] {}% download progressing...", filePath, decimalFormat.format(percentTransferred));
            }
            log.info("[{}] download directory from S3 success!", filePath);
            FileUtil.rename(localDirectory);
            FileUtil.moveFilesToTopLevel(localDirectory);
            log.info("compressing to zip file...");
            addFolderToZip(zipOut, localDirectory);
        } catch (Exception e) {
            log.error("ZIP 파일 압축 중 오류 발생", e);
            throw new RuntimeException("ZIP 파일 압축 중 오류 발생");
        } finally {
            zipOut.close();
            FileUtil.remove(localDirectory);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        String fileName = extractFileName(filePath, REGEX);
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    @Override
    public void deleteFolder(String folderPath) {
        String fileName = extractFileName(folderPath, REGEX);
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(fileName);

        ListObjectsV2Result listObjectsV2Result;
        do {
            listObjectsV2Result = amazonS3Client.listObjectsV2(listObjectsV2Request);

            List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();
            for (S3ObjectSummary objectSummary : objectSummaries) {
                amazonS3Client.deleteObject(bucket, objectSummary.getKey());
            }

            listObjectsV2Request.setContinuationToken(listObjectsV2Result.getNextContinuationToken());
        } while (listObjectsV2Result.isTruncated());
    }

    @Override
    public String getOriginName(String saveName) {
        String splitStr = ".com/";
        int startIndex = saveName.lastIndexOf(splitStr);

        if(startIndex != -1) {
            startIndex += splitStr.length();
            saveName = saveName.substring(startIndex);
        }

        int uuidIndex = saveName.indexOf("_") + 1;
        String originName = saveName.substring(uuidIndex);
        return originName;
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
    private void validateFileExists(String fileName) throws FileNotFoundException {
        if (!amazonS3Client.doesObjectExist(bucket, fileName))
            throw new FileNotFoundException();
    }
}
