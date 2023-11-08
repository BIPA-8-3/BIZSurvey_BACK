package com.bipa.bizsurvey.domain.community.domain;

import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @ColumnDefault("0")
    private int count;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 추 후 redis caching
    public int addCount(){
        this.count += 1;
        return this.count;
    }

}
