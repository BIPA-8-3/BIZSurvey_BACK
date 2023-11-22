package com.bipa.bizsurvey.domain.workspace.application;


import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceDto;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceDto.ListResponse create(Long userId, WorkspaceDto.CreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Workspace workspace = Workspace.builder()
                .workspaceName(request.getWorkspaceName())
                .workspaceType(request.getWorkspaceType())
                .user(user)
                .build();

        workspaceRepository.save(workspace);

        return WorkspaceDto.ListResponse.builder()
                    .workspaceName(workspace.getWorkspaceName())
                    .workspaceType(workspace.getWorkspaceType())
                    .id(workspace.getId())
                    .build();
    }

    @Transactional(readOnly = true)
    public List<WorkspaceDto.ListResponse> listWorkspaces(Long userId) {
        List<Workspace> list = workspaceRepository.findWorkspacesByUserIdAndDelFlagFalse(userId);

        return list.stream().map(e ->
                WorkspaceDto.ListResponse.builder()
                        .workspaceName(e.getWorkspaceName())
                        .workspaceType(e.getWorkspaceType())
                        .id(e.getId()).build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkspaceDto.ReadResponse readOne(Long id) {
        Workspace workspace = getWorkspace(id);

        WorkspaceDto.ReadResponse response = WorkspaceDto.ReadResponse.builder()
                .id(workspace.getId())
                .workspaceType(workspace.getWorkspaceType())
                .workspaceName(workspace.getWorkspaceName())
                .email(workspace.getUser().getEmail())
                .regDate(workspace.getRegDate())
                .modDate(workspace.getModDate())
                .build();

        return response;
    }

    public void update(Long workspaceId, WorkspaceDto.UpdateRequest request) {
        Workspace workspace = getWorkspace(workspaceId);
        workspace.updateWorkspace(request.getWorkspaceName());
    }

    public void delete(Long workspaceId) {
         Workspace workspace = getWorkspace(workspaceId);
         workspace.delete();
    }

    private Workspace getWorkspace(Long id) {
        return workspaceRepository.findWorkspaceByIdAndDelFlagFalse(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스 입니다."));
    }
}

