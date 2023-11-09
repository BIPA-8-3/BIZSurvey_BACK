package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}
