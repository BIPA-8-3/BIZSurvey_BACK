package com.bipa.bizsurvey.domain.sharedSurveyResponse.domain;


import com.bipa.bizsurvey.domain.question.domain.Question;
import com.bipa.bizsurvey.domain.sharedList.domain.SharedList;
import com.bipa.bizsurvey.domain.sharedSurvey.domain.SharedSurvey;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "shared_survey_response")
public class SharedSurveyResponse extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shared_survey_response_id")
    private Long id;

    private String surveyAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_survey_id")
    private SharedSurvey sharedSurvey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_list_id")
    private SharedList sharedList;

}
