package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.SharedList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedListRepository extends JpaRepository<SharedList, Long> {
    Optional<SharedList> findByIdAndDelFlagFalse(Long id);
    List<SharedList> findSharedListsBySharedSurveyIdAndDelFlagFalse(Long sharedSurveyId);
}
