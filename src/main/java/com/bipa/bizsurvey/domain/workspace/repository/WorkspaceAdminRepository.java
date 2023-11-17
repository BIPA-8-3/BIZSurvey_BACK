package com.bipa.bizsurvey.domain.workspace.repository;


import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.domain.WorkspaceAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceAdminRepository extends JpaRepository<WorkspaceAdmin, Long> {
    WorkspaceAdmin findByWorkspaceIdAndUserId(Long userId, Long workspaceId);
}
