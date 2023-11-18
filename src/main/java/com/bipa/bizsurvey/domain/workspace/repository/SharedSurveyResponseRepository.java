package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.SharedSurvey;
import com.bipa.bizsurvey.domain.workspace.domain.SharedSurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedSurveyResponseRepository extends JpaRepository<SharedSurveyResponse, Long> {
}
