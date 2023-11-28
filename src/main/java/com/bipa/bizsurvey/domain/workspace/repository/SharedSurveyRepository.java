package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.SharedSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedSurveyRepository extends JpaRepository<SharedSurvey, Long> {
    Optional<SharedSurvey> findByIdAndDelFlagFalse(Long id);
    List<SharedSurvey> findBySurveyIdAndDelFlagFalse(Long surveyId);
}
