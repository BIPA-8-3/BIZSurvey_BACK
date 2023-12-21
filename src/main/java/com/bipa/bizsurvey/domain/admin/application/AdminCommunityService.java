package com.bipa.bizsurvey.domain.admin.application;

import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.domain.QSurveyPost;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.dto.response.surveyPost.SurveyPostCardResponse;
import com.bipa.bizsurvey.domain.community.dto.response.surveyPost.SurveyPostResponse;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.global.common.CustomPageImpl;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCommunityService {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPost p = QPost.post;
    private final QSurveyPost sp = QSurveyPost.surveyPost;

    public Page<?> getSurveyPostList(Pageable pageable){

        long totalCount = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.postType.eq(PostType.SURVEY))
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .stream().count();


        List<Tuple> tupleList = jpaQueryFactory.
                select(
                        p.id,
                        p.title,
                        p.content,
                        p.count,
                        p.user.nickname,
                        p.regDate,
                        sp.id,
                        sp.startDateTime,
                        sp.endDateTime,
                        sp.thumbImgUrl
                )
                .from(p)
                .innerJoin(sp).on(p.id.eq(sp.post.id))
                .where(p.delFlag.eq(false))
                .orderBy(p.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<SurveyPostResponse> results = new ArrayList<>();

        for(Tuple tuple : tupleList){

            SurveyPostResponse surveyPostResponse = SurveyPostResponse.builder()
                    .postId(tuple.get(p.id))
                    .title(tuple.get(p.title))
                    .content(tuple.get(p.content))
                    .count(tuple.get(p.count))
                    .createDate(tuple.get(p.regDate).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .nickname(tuple.get(p.user.nickname))
                    .startDateTime(tuple.get(sp.startDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .endDateTime(tuple.get(sp.endDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .surveyId(tuple.get(sp.survey.id))
                    .thumbImageUrl(tuple.get(sp.thumbImgUrl))
                    .build();

            results.add(surveyPostResponse);
        }
        return new CustomPageImpl<>(results, pageable.getPageNumber(), pageable.getPageSize(), totalCount);
    }
}
