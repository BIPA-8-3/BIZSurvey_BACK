package com.bipa.bizsurvey.domain.community.dto.response;

import com.bipa.bizsurvey.domain.community.enums.PostType;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
public class PostResponse {
    private Long postId;

    private String title;

    private String content;

    private int count;

    private String nickname;
}
