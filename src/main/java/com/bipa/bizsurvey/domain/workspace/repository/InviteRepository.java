package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, Long> {
}
