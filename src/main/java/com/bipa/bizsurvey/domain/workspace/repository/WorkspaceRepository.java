package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findWorkspaceByIdAndDelFlagFalse(Long id);

    List<Workspace> findWorkspacesByUserIdAndDelFlagFalse(Long userId);

    Workspace findByUserIdAndWorkspaceType(Long userId, WorkspaceType workspaceType);

    // 기업 회원
    @Query("select w from Workspace w " +
            "where w.delFlag = false " +
            "and (w.user.id = :userId " +
            "       or w in (select wa.workspace " +
            "                   from WorkspaceAdmin wa " +
            "                   where wa.delFlag = false " +
            "                   and wa.user.id = :userId)) " +
            "order by w.workspaceType DESC, w.regDate asc ")
    List<Workspace> findCompanyPlanWorkspaces(@Param("userId") Long userId);

    // 개인 회원
    @Query("select w from Workspace w " +
            "where w.delFlag = false " +
            "and ((w.user.id = :userId " +
            "       and w.workspaceType = :workspaceType)" +
            "       or w in (select wa.workspace " +
            "                   from WorkspaceAdmin wa " +
            "                   where wa.delFlag = false " +
            "                   and wa.user.id = :userId)) " +
            "order by w.workspaceType DESC, w.regDate asc ")
    List<Workspace> findPersonalPlanWorkspaces(@Param("userId") Long userId, @Param("workspaceType") WorkspaceType workspaceType);

    // 커뮤니티 회원
    @Query("select wa.workspace from WorkspaceAdmin wa where wa.delFlag = false and wa.user.id = :userId ")
    List<Workspace> findCommunityPlanWorkspaces(@Param("userId") Long userId);
    Optional<Workspace> findByDelFlagFalseAndUserIdAndWorkspaceType(Long userId, WorkspaceType workspaceType);
}
