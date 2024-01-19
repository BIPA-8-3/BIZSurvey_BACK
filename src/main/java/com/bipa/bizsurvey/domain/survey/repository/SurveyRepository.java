package com.bipa.bizsurvey.domain.survey.repository;

import com.bipa.bizsurvey.domain.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    List<Survey> findAllByWorkspaceId(Long workspaceId);

    @Query(value = "SELECT s " +
            "FROM Survey s " +
            "WHERE s.delFlag = false " +
            "AND s.workspace.id IN " +
            "(SELECT w.id " +
            " FROM Workspace w " +
            " WHERE w.delFlag = false " +
            " AND w.user.id = :userId) " +
            "OR s.workspace.id IN " +
            "(SELECT wa.workspace.id " +
            " FROM WorkspaceAdmin wa " +
            " WHERE wa.delFlag = false " +
//            " AND wa.inviteFlag = true " +
            " AND wa.user.id = :userId)")
    List<Survey> getSurveyList(@Param("userId") Long userId);

    Optional<Survey> findByIdAndDelFlagFalse(Long id);


}
