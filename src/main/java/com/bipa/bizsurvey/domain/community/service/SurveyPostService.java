package com.bipa.bizsurvey.domain.community.service;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.domain.QSurveyPost;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.CreateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.UpdateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.dto.response.surveyPost.SurveyPostResponse;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.global.common.sorting.OrderByNull;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class SurveyPostService {

    private final SurveyPostRepository surveyPostRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SurveyService surveyService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final QPost p = QPost.post;
    private final QSurveyPost sp = QSurveyPost.surveyPost;

    public void createSurveyPost(Long userId, CreateSurveyPostRequest createSurveyPostRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Post post = Post.builder()
                .user(user)
                .postType(PostType.SURVEY)
                .title(createSurveyPostRequest.getTitle())
                .content(createSurveyPostRequest.getContent())
                .build();
        Post save = postRepository.save(post);

        Survey survey = surveyService.findSurvey(createSurveyPostRequest.getSurveyId());

        SurveyPost surveyPost = SurveyPost.builder()
                .startDateTime(createSurveyPostRequest.getStartDateTime())
                .endDateTime(createSurveyPostRequest.getEndDateTime())
                .maxMember(createSurveyPostRequest.getMaxMember())
                .post(save)
                .survey(survey)
                .build();

        surveyPostRepository.save(surveyPost);
    }

    // 상세 조회
    public SurveyPostResponse getSurveyPost(Long postId){
         Tuple tuple = jpaQueryFactory
                 .select(
                         p.id,
                         p.title,
                         p.content,
                         p.count,
                         p.user.nickname,
                         p.regDate,
                         sp.maxMember,
                         sp.startDateTime,
                         sp.endDateTime
                 )
                 .from(p)
                 .innerJoin(sp).on(p.eq(sp.post))
                 .where(p.id.eq(postId))
                 .where(p.delFlag.eq(false))
                 .where(p.reported.eq(false))
                 .fetchOne();

         Post post = postService.findPost(postId);
         post.addCount(); // 조회수 증가

        return SurveyPostResponse.builder()
                .postId(tuple.get(p.id))
                .title(tuple.get(p.title))
                .content(tuple.get(p.content))
                .count(tuple.get(p.count))
                .createDate(tuple.get(p.regDate).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .nickname(tuple.get(p.user.nickname))
                .maxMember(tuple.get(sp.maxMember))
                .startDateTime(tuple.get(sp.startDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .endDateTime(tuple.get(sp.endDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .commentList(commentService.getCommentList(postId))
                .build();
    }

    // 전체 조회
    public Page<?> getSurveyPostList(Pageable pageable, String fieldName){

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
                        sp.maxMember,
                        sp.startDateTime,
                        sp.endDateTime
                )
                .from(p)
                .innerJoin(sp).on(p.id.eq(sp.post.id))
                .where(p.delFlag.eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortByField(fieldName))
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
                    .maxMember(tuple.get(sp.maxMember))
                    .startDateTime(tuple.get(sp.startDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .endDateTime(tuple.get(sp.endDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .build();
            results.add(surveyPostResponse);
        }
        return new PageImpl<>(results, pageable, totalCount);
    }


    // 검색
    public Page<?> searchSurveyPost(SearchPostRequest searchPostRequest, Pageable pageable){
        long totalCount = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.COMMUNITY))
                .where(p.content.like("%" + searchPostRequest.getKeyword() + "%")
                        .or(p.title.like("%" + searchPostRequest.getKeyword() + "%")))
                .stream().count();

        List<Tuple> tupleList = jpaQueryFactory.
                select(
                        p.id,
                        p.title,
                        p.content,
                        p.count,
                        p.user.nickname,
                        p.regDate,
                        sp.maxMember,
                        sp.startDateTime,
                        sp.endDateTime
                )
                .from(p)
                .innerJoin(sp).on(p.eq(sp.post))
                .where(p.delFlag.eq(false))
                .where(p.content.like("%" + searchPostRequest.getKeyword() + "%")
                        .or(p.title.like("%" + searchPostRequest.getKeyword() + "%")))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(p.count.desc())
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
                    .maxMember(tuple.get(sp.maxMember))
                    .startDateTime(tuple.get(sp.startDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .endDateTime(tuple.get(sp.endDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                    .build();
            results.add(surveyPostResponse);
        }
        return new PageImpl<>(results, pageable, totalCount);

    }

    public void updateSurveyPost(Long userId, Long postId, UpdateSurveyPostRequest updateSurveyPostRequest){
        postService.checkPermission(userId, postId);
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyService.findSurvey(updateSurveyPostRequest.getSurveyId());
        surveyPost.updateSurveyPost(updateSurveyPostRequest, survey);
        surveyPostRepository.save(surveyPost);
    }


    private OrderSpecifier<?> sortByField(String filedName){

        Order order = Order.DESC;

        if(Objects.isNull(filedName)){
            return new OrderSpecifier<>(order, p.id);
        }

        if(filedName.equals("count")){
            return new OrderSpecifier<>(order, p.count);
        }

        if(filedName.equals("regDate")){
            return new OrderSpecifier<>(order, p.regDate);
        }

        return OrderByNull.getDefault();
    }


}
