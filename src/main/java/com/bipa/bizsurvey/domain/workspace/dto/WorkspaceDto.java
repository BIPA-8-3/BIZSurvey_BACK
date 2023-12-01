
package com.bipa.bizsurvey.domain.workspace.dto;


import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class WorkspaceDto {
    @Data
    public static class CreateRequest {
        private String workspaceName;
        private WorkspaceType workspaceType;
    }

    @Data
    public static class ReadRequest {
        private Long id;
    }

    @Data
    public static class UpdateRequest{
        private String workspaceName;
    }

    @Data
    public static class DeleteRequest {
        private Long Id;
    }

    @Data
    @Builder
    public static class ListResponse {
        private Long id;
        private String workspaceName;
        private WorkspaceType workspaceType;
    }

    @Data
    @Builder
    public static class ReadResponse {
        private Long id;
        private String workspaceName;
        private WorkspaceType workspaceType;
        private String email;
        LocalDateTime regDate;
        LocalDateTime modDate;
    }
}