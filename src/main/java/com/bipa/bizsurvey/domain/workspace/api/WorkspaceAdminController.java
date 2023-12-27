package com.bipa.bizsurvey.domain.workspace.api;

import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.workspace.application.WorkspaceAdminService;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import com.bipa.bizsurvey.global.common.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace/admin")
@Log4j2
public class WorkspaceAdminController {
    private final WorkspaceAdminService workspaceAdminService;
    private final RedisService redisService;

    @PostMapping("/invite")
    public ResponseEntity<WorkspaceAdminDto.Response> invite(@AuthenticationPrincipal LoginUser loginUser,
                                                             @RequestBody WorkspaceAdminDto.InviteRequest request) {
        WorkspaceAdminDto.Response response = null;
        try {
            response = workspaceAdminService.invite(request);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/list/{workspaceId}")
                .buildAndExpand(request.getWorkspaceId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/re-invite")
    public ResponseEntity<WorkspaceAdminDto.Response> reinvite(@AuthenticationPrincipal LoginUser loginUser,
                                                               @RequestBody WorkspaceAdminDto.ReInviteRequest request) {
        WorkspaceAdminDto.Response response = null;
        try {
            response = workspaceAdminService.reinvite(request);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/list/{workspaceId}")
                .buildAndExpand(response.getWorkspaceId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping
    public ResponseEntity<?> acceptInvite(@AuthenticationPrincipal LoginUser loginUser,
                                          @RequestBody WorkspaceAdminDto.AcceptRequest request) {
        try {
            request.setUserId(loginUser.getId());
            return ResponseEntity.ok().body(workspaceAdminService.acceptInvite(request));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list/{workspaceId}")
    public ResponseEntity<WorkspaceAdminDto.ListResponse> list(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long workspaceId) {
        return ResponseEntity.ok().body(workspaceAdminService.list(workspaceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        try {
            workspaceAdminService.delete(id);
            return ResponseEntity.ok().body("삭제가 완료되었습니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/invite/{token}")
    public ResponseEntity<String> checkInvitationCode(@PathVariable String token) {
        try {
            if (workspaceAdminService.tokenValueVerification(token)) {
                return ResponseEntity.ok().body(token);
            } else {
                return ResponseEntity.badRequest().body("유효하지 않은 링크입니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("위조된 링크 입니다.");
        }
    }
}
// redis 관련 코드 제거
//    @GetMapping("/invite/{token}")
//    public ResponseEntity<String> checkInvitationCode(@PathVariable String token) {
//        if(!redisService.validateDataExists("INVITE-" + token)) {
//            return ResponseEntity.ok().body(token);
//        }else {
//            return ResponseEntity.badRequest().body("유효하지 않은 링크입니다.");
//        }
//    }
