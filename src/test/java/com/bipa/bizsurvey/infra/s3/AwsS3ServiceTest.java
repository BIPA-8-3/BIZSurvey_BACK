package com.bipa.bizsurvey.infra.s3;


import com.bipa.bizsurvey.global.common.storage.Domain;
import com.bipa.bizsurvey.global.common.storage.StorageService;
import com.bipa.bizsurvey.global.common.storage.ShareType;
import com.bipa.bizsurvey.global.common.storage.UploadSurveyAnswerRequest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@ActiveProfiles("test")
public class AwsS3ServiceTest {

    @Autowired
    private StorageService awsS3Service;

    @Test
    @DisplayName("AWS S3 Service - UploadFile Test")
    public void testUploadFile() throws IOException {
        // 테스트할 MultipartFile 객체 생성
        MockMultipartFile file = new MockMultipartFile(
                "테스트파일1.txt",
                "한글텍스트파일1.txt",
                "text/plain",
                ("한글한글").getBytes()
        );
        String url = awsS3Service.uploadFile(file, Domain.USER, null);

        assertNotNull(url);
        log.info("url : " + url);
    }

    @Test
    @DisplayName("AWS S3 Service - UploadFile Test")
    public void testUploadSurveyFile() throws IOException {
        // 테스트할 MultipartFile 객체 생성
        MockMultipartFile file = new MockMultipartFile(
                "테스트파일2.txt",
                "한글텍스트파일2.txt",
                "text/plain",
                ("한글한글").getBytes()
        );

        UploadSurveyAnswerRequest dto = UploadSurveyAnswerRequest.builder()
                .file(file)
                .surveyId(1L)
                .shareType(ShareType.EXTERNAL)
                .shareId(1L)
                .questionId(1L)
                .build();

        String url = awsS3Service.uploadFile(dto.getFile(), dto.getDomain(), dto.getPath());

        assertNotNull(url);
        log.info("url : " + url);
    }

    @Test
    public void testDownloadFile() throws IOException {
        String fileUrl = "https://bizsurvey-bucket.s3.ap-northeast-2.amazonaws.com/files/user/b3373c47-ae22-41ee-891c-2376800c20fa.txt";

        byte[] bytes = awsS3Service.downloadFile(fileUrl);
        assertEquals(HttpStatus.OK, bytes);

        Path localFilePath = Path.of("downloaded-file.txt");
        Files.write(localFilePath, bytes);

        assertTrue(Files.exists(localFilePath));
    }

    @Test
    public void testDeleteImage() {
        String fileUrl = "https://bizsurvey-bucket.s3.ap-northeast-2.amazonaws.com/images/survey/052f468b-ced8-4b25-9ea9-33d477b66e2d.jpg";
        awsS3Service.deleteFile(fileUrl);
    }
}