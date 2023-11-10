package com.bipa.bizsurvey.domain.survey.repository;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSurveyResponseRepository extends JpaRepository<UserSurveyResponse, Long> {

    @Query(value = "SELECT u FROM UserSurveyResponse u JOIN FETCH u.question q WHERE u.surveyPost = :postId ORDER BY q.id ASC")
    List<UserSurveyResponse> findAllByPostId(@Param("postId") SurveyPost surveyPost);

    @Query(value = "SELECT DISTINCT u.user.nickname FROM UserSurveyResponse u WHERE u.surveyPost = :surveyPostId")
    List<String> findDistinctNicknamesBySurveyPostId(@Param("surveyPostId") SurveyPost surveyPost);


}
