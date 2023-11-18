package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.SharedList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedListRepository extends JpaRepository<SharedList, Long> {
}
