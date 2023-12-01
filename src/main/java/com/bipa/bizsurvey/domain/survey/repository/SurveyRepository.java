package com.bipa.bizsurvey.domain.survey.repository;

import com.bipa.bizsurvey.domain.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findAllByWorkspaceId(Long workspaceId);

    @Query(value = "SELECT s.survey_id AS surveyId, s.title AS title, w.workspace_name AS workspaceName, w.workspace_type AS workspaceType " +
            "FROM survey s " +
            "INNER JOIN workspace w ON s.workspace_id = w.workspace_id " +
            "LEFT JOIN (" +
            "    SELECT w.workspace_id " +
            "    FROM workspace_admin a " +
            "    RIGHT JOIN workspace w ON a.workspace_id = w.workspace_id " +
            "    WHERE a.user_id = :userId AND (w.del_flag = false and a.del_flag = false) OR (w.user_id = :userId AND w.workspace_type = 'PERSONAL')" +
            ") subquery ON s.workspace_id = subquery.workspace_id " +
            "WHERE s.del_flag = false AND subquery.workspace_id IS NOT NULL", nativeQuery = true)
    List<Object[]> getSurveyList(@Param("userId") Long userId);

    Optional<Survey> findByIdAndDelFlagFalse(Long id);
}
