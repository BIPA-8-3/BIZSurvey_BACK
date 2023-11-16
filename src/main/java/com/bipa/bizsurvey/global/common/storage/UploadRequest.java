package com.bipa.bizsurvey.global.common.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
public class UploadRequest {
    MultipartFile file;
    Domain domain;
}
