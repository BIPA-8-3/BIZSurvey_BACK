package com.bipa.bizsurvey.domain.survey.repository;

import com.bipa.bizsurvey.domain.survey.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    //
    List<Question> findAllBySurveyId(Long surveyId);
    List<Question> findAllBySurveyIdAndDelFlagFalseOrderByStep(Long surveyKey);
    // 필 수 질 문  추 출
    List<Long> findIdByDelFlagFalseAndSurveyIdAndIsRequiredTrue(Long surveyId);
    List<Question> findByIdInAndDelFlagFalseOrderById(List<Long> ids);
}
