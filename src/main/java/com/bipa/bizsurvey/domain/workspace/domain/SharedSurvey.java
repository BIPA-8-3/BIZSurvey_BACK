package com.bipa.bizsurvey.domain.workspace.domain;

import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "shared_survey")
public class SharedSurvey extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shared_survey_id")
    private Long id;

    private LocalDateTime deadlineDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SharedSurvey(Survey survey) {
        this.survey = survey;
    }

    @PrePersist
    public void prePersist() {
        if (deadlineDate == null) {
            deadlineDate = LocalDateTime.now().plusDays(7);
        }
    }

    public void updateDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }
}
