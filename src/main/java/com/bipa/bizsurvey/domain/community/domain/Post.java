package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.community.dto.request.post.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "post")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ColumnDefault("0")
    private int count;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @ColumnDefault("false")
    @Column(insertable = false)
    private Boolean reported;
    // 신고 당했는지 여부

    @Setter
    @Column(nullable = true)
    private Long voteId;



    @Builder
    public Post(String title, String content, PostType postType, User user) {
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.user = user;
    }

    public static Post toEntity(User user, PostType postType, CreatePostRequest createPostRequest){
        return Post.builder()
                .title(createPostRequest.getTitle())
                .content(createPostRequest.getContent())
                .postType(postType)
                .user(user)
                .build();
    }

    public void updatePost(UpdatePostRequest updatePostRequest){
        this.title = updatePostRequest.getTitle();
        this.content = updatePostRequest.getContent();
    }

    public void updateDelFlag(){
        setDelFlag(true);
    }


    // 추 후 redis caching
    public void addCount(){
        this.count += 1;
    }

}
