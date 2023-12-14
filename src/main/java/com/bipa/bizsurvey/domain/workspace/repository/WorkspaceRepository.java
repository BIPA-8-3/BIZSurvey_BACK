package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findWorkspaceByIdAndDelFlagFalse(Long id);

    List<Workspace> findWorkspacesByUserIdAndDelFlagFalse(Long userId);

    Workspace findByUserIdAndWorkspaceType(Long userId, WorkspaceType workspaceType);


    @Query("select w from Workspace w " +
            "where w.delFlag = false " +
            "and (w.user.id = :userId " +
            "or w in (select wa.workspace " +
                        "from WorkspaceAdmin wa " +
                        "where wa.delFlag = false " +
                        "and wa.user.id = :userId))")
    List<Workspace> findWorkspaceByDelFlagFalseAndUserId(Long userId);

// select * from workspace where user_id = 1 and workspace_type = 'PERSONAL';
    Optional<Workspace> findByDelFlagFalseAndUserIdAndWorkspaceType(Long userId, WorkspaceType workspaceType);
}
