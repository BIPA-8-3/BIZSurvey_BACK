package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.user.domain.User;
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
@Table(name = "vote_user_answer")
public class VoteUserAnswer extends BaseEntity {

    //


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_user_answer_id")
    private Long id;

    @Column(nullable = false)
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Builder
    public VoteUserAnswer(String answer, User user, Vote vote) {
        this.answer = answer;
        this.user = user;
        this.vote = vote;
    }
}
