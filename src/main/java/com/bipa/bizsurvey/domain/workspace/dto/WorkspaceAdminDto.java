package com.bipa.bizsurvey.domain.workspace.dto;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.enums.AdminType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.List;

public class WorkspaceAdminDto {

    @Data
    public static class InviteRequest {
        private Long workspaceId;
        private String email;
        private AdminType adminType;
    }

    @Data
    public static class ReInviteRequest {
        private Long id;
    }

    @Data
    public static class AcceptRequest {
        private Long userId;
        private String token;
    }

    @Data
    public static class CreateRequest {
        private Long id;
        private Long userId;
    }

    @Data
    public static class DeleteRequest {
        private Long id;
        private Long userId;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TemporaryUserInfo {
        private Long id;
        private Long workspaceId;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private Long workspaceId;
        private String email;
        private String name;
        private String nickName;
        private AdminType adminType;
        private Boolean inviteFlag;
        private Boolean hasToken;
    }

    @Data
    @Builder
    public static class ListResponse {
        List<Response> adminList;
        List<Response> waitList;
    }
}
