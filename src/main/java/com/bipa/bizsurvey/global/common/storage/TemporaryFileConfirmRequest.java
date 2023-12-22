package com.bipa.bizsurvey.global.common.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class TemporaryFileConfirmRequest {
    String tempFileName;
    String fileName;
}
