package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.dto.request.comment.UpdateCommentRequest;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @ColumnDefault("false")
    @Column(insertable = false)
    private Boolean reported;
    // 신고 당했는지 여부

    @Builder
    public Comment(String content, Post post, User user) {
        this.content = content;
        this.post = post;
        this.user = user;
    }

    public void updateContent(UpdateCommentRequest updateCommentRequest){
        this.content = updateCommentRequest.getContent();
    }

    public void updateDelFlag(){
        setDelFlag(true);
    }

    public void updateReported(){
        this.reported = true;
    }

    public void updateReportedFalse(){
        this.reported = false;
    }
}
