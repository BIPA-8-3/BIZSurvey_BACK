package com.bipa.bizsurvey.domain.workspace.repository;


import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.domain.WorkspaceAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkspaceAdminRepository extends JpaRepository<WorkspaceAdmin, Long> {
    Optional<WorkspaceAdmin> findByIdAndDelFlagFalse(Long id);
    Optional<WorkspaceAdmin> findByWorkspaceIdAndUserId(Long userId, Long workspaceId);
    List<WorkspaceAdmin> findByWorkspaceIdAndDelFlagFalse(Long workspaceId);
    List<WorkspaceAdmin> findByUserIdAndDelFlagFalse(Long userId);
}
