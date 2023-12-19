package com.bipa.bizsurvey.domain.workspace.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.parameters.P;

public class ContactDto {
    @Data
    public static class CreateRequest {
        private String name;
        private String email;
//        private String remark;
        private Long workspaceId;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String email;
//        private String remark;
    }

    @Data
    public static class SearchRequest {
        private Long workspaceId;
        private String keyword;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String email;
//        private String remark;
    }

    @Data
    public static class SharedRequest {
        private Long id;
        private String email;
    }
}
