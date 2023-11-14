package com.bipa.bizsurvey.infra.s3;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
@Log4j2
public class S3Controller {
    private final AwsS3Service awsS3Service;
    @GetMapping(value = "/download/zip/**", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadZip(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            response.addHeader("Content-Disposition", "attachment; filename=" + new String((RandomStringUtils.randomAlphanumeric(6) + "-download.zip").getBytes("UTF-8"), "ISO-8859-1"));
            String prefix = getPrefix(request.getRequestURI(), "/s3/download/zip2/");
            awsS3Service.downloadZip(prefix, response);
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드에 실패했습니다.");
        }
    }

    @GetMapping("/download/file/**")
    public ResponseEntity<?> downloadFile(HttpServletRequest request) {
        String fileUrl = getPrefix(request.getRequestURI(), "/s3/download/file/");
        try {
            return awsS3Service.downloadFile(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 다운로드에 실패했습니다.");
        }
    }

    private String getPrefix(String uri, String regex) {
        String[] split = uri.split(regex);
        return split.length < 2 ? "" : split[1];
    }
}
