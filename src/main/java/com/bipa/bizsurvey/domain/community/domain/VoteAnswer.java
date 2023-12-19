package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.community.domain.Vote;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "vote_answer")
public class VoteAnswer extends BaseEntity {

    //


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_answer_id")
    private Long id;

    @Column(nullable = false)
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    Vote vote;

    @Builder
    public VoteAnswer(String answer, Vote vote) {
        this.answer = answer;
        this.vote = vote;
    }
}
