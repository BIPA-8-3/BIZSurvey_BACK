package com.bipa.bizsurvey.infra.s3;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final AwsS3Service awsS3Service;

    @GetMapping(value = "/download/zip/**", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadZip(HttpServletRequest request) throws IOException, InterruptedException {
        String prefix = getPrefix(request.getRequestURI(), "/s3/download/zip/");

        Resource resource = awsS3Service.downloadZip(prefix);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDispositionFormData("attachment", new String(resource.getFilename().getBytes("UTF-8"), "ISO-8859-1"));
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/download/file/{originName}/{encodedFileUrl}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String originName, @PathVariable String encodedFileUrl) throws IOException {
        String fileUrl = URLDecoder.decode(encodedFileUrl, StandardCharsets.UTF_8.name());
        return awsS3Service.downloadFile(fileUrl, originName);
    }

    private String getPrefix(String uri, String regex) {
        String[] split = uri.split(regex);
        return split.length < 2 ? "" : split[1];
    }
}
