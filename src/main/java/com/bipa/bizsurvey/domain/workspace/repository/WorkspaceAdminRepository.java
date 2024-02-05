package com.bipa.bizsurvey.domain.workspace.repository;


import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.domain.WorkspaceAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkspaceAdminRepository extends JpaRepository<WorkspaceAdmin, Long> {
    Optional<WorkspaceAdmin> findByIdAndDelFlagFalse(Long id);
    Optional<WorkspaceAdmin> findByDelFlagFalseAndWorkspaceIdAndUserId(Long userId, Long workspaceId);
    Optional<WorkspaceAdmin> findByUserIdAndWorkspaceIdAndDelFlagFalse(Long userId, Long WorkspaceId);
    List<WorkspaceAdmin> findByWorkspaceIdAndDelFlagFalse(Long workspaceId);
    List<WorkspaceAdmin> findByUserIdAndDelFlagFalse(Long userId);

    @Query("select wa.user.id from WorkspaceAdmin wa " +
            "where wa.delFlag = false " +
            "and wa.workspace.id = :workspaceId")
    List<Long> findUserByWorkspaceId(@Param("workspaceId") Long workspaceId);
}
