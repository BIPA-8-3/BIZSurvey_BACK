package com.bipa.bizsurvey.domain.survey.repository;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import com.bipa.bizsurvey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface UserSurveyResponseRepository extends JpaRepository<UserSurveyResponse, Long> {

    @Query(value = "SELECT u FROM UserSurveyResponse u JOIN FETCH u.question q WHERE u.surveyPost = :postId ORDER BY q.id ASC")
    List<UserSurveyResponse> findAllByPostId(@Param("postId") SurveyPost surveyPost);

    @Query(value = "SELECT DISTINCT u.user.nickname FROM UserSurveyResponse u WHERE u.surveyPost = :surveyPostId")
    List<String> findNicknamesBySurveyPostId(@Param("surveyPostId") SurveyPost surveyPost);

    UserSurveyResponse findBySurveyPostIdAndUserId(Long surveyPostId, Long userId);
}