package com.bipa.bizsurvey.domain.workspace.domain;

import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "shared_survey")
public class SharedSurvey extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shared_survey_id")
    private Long id;

    @ColumnDefault("7")
    private Long deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SharedSurvey(Survey survey, Long deadline) {
        this.survey = survey;
        this.deadline = deadline;
    }

    public void plusDeadline() {
        this.deadline += 7;
    }

}
