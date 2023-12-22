package com.bipa.bizsurvey.global.common.storage;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@Log4j2
public class StorageController {
    private final StorageService service;

    @PostMapping("/")
    public ResponseEntity<?> uploadFile(UploadRequest request) {
        return ResponseEntity.ok().body(service.uploadFile(request.getFile(), request.getDomain(), null));
    }

    @PostMapping("/survey")
    public ResponseEntity<?> uploadSurveyFile(UploadSurveyAnswerRequest request) {
        return ResponseEntity.ok().body(service.uploadFile(request.getFile(), request.getDomain(), request.getPath()));
    }

    @DeleteMapping("/file/**")
    public ResponseEntity<?> deleteFile(HttpServletRequest request) {
        String fileUrl = service.extractFileName(request.getRequestURI(), "/storage/file/");
        service.deleteFile(fileUrl);
        return ResponseEntity.ok().body("정상적으로 삭제되었습니다.");
    }
    @DeleteMapping("/folder/**")
    public ResponseEntity<?> deleteFolder(HttpServletRequest request) {
        String fileUrl = service.extractFileName(request.getRequestURI(), "/storage/folder/");
        service.deleteFolder(fileUrl);
        return ResponseEntity.ok().body("정상적으로 삭제되었습니다.");
    }

    @PostMapping("/multiple/files")
    public ResponseEntity<?> deleteMultipleFiles(@RequestBody List<DeleteFileRequest> fileList) {
        fileList.stream().forEach(log::info);
        service.deleteMultipleFiles(fileList);
        return ResponseEntity.ok().body("정상적으로 삭제되었습니다.");
    }

    @GetMapping("/file/**")
    public ResponseEntity<?> downloadFile(HttpServletRequest request) {
        String fileFullName = service.extractFileName(request.getRequestURI(), "/storage/file/");
        try {
            byte[] bytes = service.downloadFile(fileFullName);

            HttpHeaders httpHeaders = new HttpHeaders();
            MediaType type = service.getContentType(service.getFileExtension(fileFullName));
            String originName = service.getOriginName(fileFullName);

            httpHeaders.setContentType(type);
            httpHeaders.setContentDispositionFormData("attachment", new String(originName.getBytes("UTF-8"), "ISO-8859-1"));

            String encodedFileName = UriUtils.encode(originName, "UTF-8");
            httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

            return ResponseEntity.ok().headers(httpHeaders).body(bytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 다운로드에 실패했습니다.");
        }
    }

    @GetMapping(value = "/folder/**", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadZip(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            response.addHeader("Content-Disposition", "attachment; filename=" + new String((RandomStringUtils.randomAlphanumeric(6) + "-download.zip").getBytes("UTF-8"), "ISO-8859-1"));
            String fileUrl = service.extractFileName(request.getRequestURI(), "/storage/folder/");
            service.downloadZip(new ZipOutputStream(response.getOutputStream()), fileUrl);
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드에 실패했습니다.");
        }
    }
}


