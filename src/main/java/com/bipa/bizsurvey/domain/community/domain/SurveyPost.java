package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.UpdateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.enums.AccessType;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "survey_post")
public class SurveyPost extends BaseEntity {


    //

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_post_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @ColumnDefault("0")
    private int score;

    private String thumbImgUrl;

    @Builder
    public SurveyPost(LocalDateTime startDateTime, LocalDateTime endDateTime, Post post, Survey survey, String thumbImgUrl) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.post = post;
        this.survey = survey;
        this.thumbImgUrl = thumbImgUrl;
    }


    public void addScore(int score){
        this.score = score;
    }


    public void updateSurveyPost(UpdateSurveyPostRequest updateSurveyPostRequest, Survey survey){
        this.post.updatePost(updateSurveyPostRequest.getTitle(), updateSurveyPostRequest.getContent());
        this.startDateTime = updateSurveyPostRequest.getStartDateTime();
        this.endDateTime = updateSurveyPostRequest.getEndDateTime();
        this.survey = survey;
        this.thumbImgUrl = updateSurveyPostRequest.getThumbImgUrl();
    }






}
