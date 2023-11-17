package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findWorkspacesByUserId(Long userId);
}
