package com.bipa.bizsurvey.domain.survey.repository;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByQuestionId(Long questionId);

    void deleteAllByQuestionId(Long questionId);
}
