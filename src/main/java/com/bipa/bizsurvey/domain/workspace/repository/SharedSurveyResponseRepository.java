package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.SharedSurvey;
import com.bipa.bizsurvey.domain.workspace.domain.SharedSurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedSurveyResponseRepository extends JpaRepository<SharedSurveyResponse, Long> {

    boolean existsByDelFlagFalseAndSharedListId(Long sharedListId);
    List<SharedSurveyResponse> findBySharedListIdAndDelFlagFalse(Long id);

}
