package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "vote")
public class Vote extends BaseEntity {


    //

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @Column(nullable = false)
    private String voteQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Vote(String voteQuestion, User user) {
        this.voteQuestion = voteQuestion;
        this.user = user;
    }
}
