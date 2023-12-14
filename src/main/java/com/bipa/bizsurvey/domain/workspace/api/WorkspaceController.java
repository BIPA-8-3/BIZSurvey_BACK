package com.bipa.bizsurvey.domain.workspace.api;

import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.workspace.application.WorkspaceService;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<?> register(@AuthenticationPrincipal LoginUser loginUser,
                                      @RequestBody WorkspaceDto.CreateRequest request) {
        WorkspaceDto.ListResponse response = workspaceService.create(loginUser.getId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDto.ReadResponse> readOne(@PathVariable Long id) {
        return ResponseEntity.ok().body(workspaceService.readOne(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<WorkspaceDto.ListResponse>> list(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseEntity.ok().body(workspaceService.listWorkspaces(loginUser.getId()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> modify(@PathVariable Long id,
                                         @RequestBody WorkspaceDto.UpdateRequest request) {
        workspaceService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        workspaceService.delete(id);
        return ResponseEntity.ok().body("삭제가 완료되었습니다.");
    }

    @PatchMapping("/survey")
    public ResponseEntity<?> modifySurveyName(@RequestBody WorkspaceDto.UpdateSurveyTitle request) {
        workspaceService.updateSurveyName(request);
        return ResponseEntity.ok().body("수정이 완료되었습니다.");

    }
    @GetMapping("/personal")
    public ResponseEntity<Boolean> readPersonalWorkspace(@AuthenticationPrincipal LoginUser loginUser) {
        try {
            workspaceService.getPersonalWorkspace(loginUser.getId());
            return ResponseEntity.ok().body(true);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.ok().body(false);
        }
    }
}
