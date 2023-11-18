package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.SharedSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedSurveyRepository extends JpaRepository<SharedSurvey, Long> {
}
