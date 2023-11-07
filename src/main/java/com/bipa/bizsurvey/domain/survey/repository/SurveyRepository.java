package com.bipa.bizsurvey.domain.survey.repository;

import com.bipa.bizsurvey.domain.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
