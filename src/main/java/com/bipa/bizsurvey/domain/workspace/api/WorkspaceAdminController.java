package com.bipa.bizsurvey.domain.workspace.api;

import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.workspace.application.WorkspaceAdminService;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import com.bipa.bizsurvey.domain.workspace.enums.AdminType;
import com.bipa.bizsurvey.domain.workspace.mq.Publisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequiredArgsConstructor
@RequestMapping("/workspace/admin")
@Log4j2
public class WorkspaceAdminController {
    private final WorkspaceAdminService workspaceAdminService;

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
    public ResponseEntity<String> acceptInvite(@AuthenticationPrincipal LoginUser loginUser,
                                               @RequestBody WorkspaceAdminDto.AcceptRequest request) {
        try {
            workspaceAdminService.acceptInvite(request);
            return ResponseEntity.ok().body("초대가 완료되었습니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list/{workspaceId}")
    public ResponseEntity<WorkspaceAdminDto.ListResponse> list(@AuthenticationPrincipal LoginUser loginUser,
                                  @PathVariable Long workspaceId) {
        return ResponseEntity.ok().body(workspaceAdminService.list(workspaceId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        try{
            workspaceAdminService.delete(id);
            return ResponseEntity.ok().body("삭제가 완료되었습니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() throws Exception {

        WorkspaceAdminDto.InviteRequest request = new WorkspaceAdminDto.InviteRequest();

        request.setWorkspaceId(1L);
        request.setEmail("hws6745@naver.com");
        request.setAdminType(AdminType.INVITED);
        workspaceAdminService.invite(request);

        return ResponseEntity.ok().body("요청완료");
    }

    private final Publisher publisher;
    @GetMapping("/test2")
    public ResponseEntity<String> test2() throws Exception {

        publisher.publish("test Message 1");
        Thread.sleep(50);
        publisher.publish("test Message 2");
        Thread.sleep(50);
        publisher.publish("test Message 3");
        Thread.sleep(50);
        publisher.publish("test Message 4");
        Thread.sleep(50);
        publisher.publish("test Message 5");
        return ResponseEntity.ok().body("요청완료");
    }
}
