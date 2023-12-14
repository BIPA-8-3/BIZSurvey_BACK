package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.community.domain.Comment;
import com.bipa.bizsurvey.domain.community.dto.request.childComment.UpdateChildCommentRequest;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "child_comment")
public class ChildComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Setter
    @ColumnDefault("false")
    @Column(insertable = false)
    private Boolean reported;
    // 신고 당했는지 여부

    public void updateDelFlag(){
        setDelFlag(true);
    }


    @Builder
    public ChildComment(String content, Comment comment, User user) {
        this.content = content;
        this.comment = comment;
        this.user = user;
    }

    public void updateChildComment(UpdateChildCommentRequest updateChildCommentRequest){
        this.content = updateChildCommentRequest.getContent();
    }

    public void updateReported(){
        this.reported = true;
    }

    public void updateReportedFalse(){
        this.reported = false;
    }


}
