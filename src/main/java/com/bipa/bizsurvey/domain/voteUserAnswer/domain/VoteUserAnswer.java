package com.bipa.bizsurvey.domain.voteUserAnswer.domain;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.vote.domain.Vote;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "vote_user_answer")
public class VoteUserAnswer {


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


}
