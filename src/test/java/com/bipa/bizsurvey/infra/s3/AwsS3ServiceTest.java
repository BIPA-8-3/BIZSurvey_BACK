package com.bipa.bizsurvey.infra.s3;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AwsS3ServiceTest {

    @Autowired
    private AwsS3Service awsS3Service;

    @Test
    @DisplayName("AWS S3 Service - UploadFile Test")
    public void testUploadFile() throws IOException {
        // 테스트할 MultipartFile 객체 생성
        MockMultipartFile file = new MockMultipartFile(
                "test-file.txt",
                "test-file.txt",
                "text/plain",
                "한글한글 한글테스트이 ㅑ얄ㅇ냘ㅇ너ㅏㅣㄹ!".getBytes()
        );
        String url = awsS3Service.uploadFile(file, S3Domain.USER);

        assertNotNull(url);
    }

    @Test
    public void testDownloadFile() throws IOException {
        // 테스트에 사용할 S3 파일 URL
        String fileUrl = "https://bizsurvey-bucket.s3.ap-northeast-2.amazonaws.com/files/user/b3373c47-ae22-41ee-891c-2376800c20fa.txt";

        ResponseEntity<byte[]> responseEntity = awsS3Service.downloadFile(fileUrl, "text.txt");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        byte[] responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        Path localFilePath = Path.of("downloaded-file.txt");
        Files.write(localFilePath, responseBody);

        assertTrue(Files.exists(localFilePath));
    }

    @Test
    public void testDeleteImage() {
        String fileUrl = "https://bizsurvey-bucket.s3.ap-northeast-2.amazonaws.com/images/survey/052f468b-ced8-4b25-9ea9-33d477b66e2d.jpg";
        awsS3Service.deleteFile(fileUrl);
    }

    @Test
    public void testGetFileName() {
        System.out.println(awsS3Service.getFileName("https://bizsurvey-bucket.s3.ap-northeast-2.amazonaws.com/7a1f68ea-0f2f-4f21-b3ec-e2ac46f42ccftesttest--test.txt"));
    }
}