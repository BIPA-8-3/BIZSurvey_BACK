package com.bipa.bizsurvey.domain.user.dto.mypage;

import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.user.domain.User;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Data
@Builder
public class UserCommunityResponse {
    private Long id;
    private String title;
    private String content;
    private int count;
    private PostType postType;
    private String nickname;
    private String regdate;
}
