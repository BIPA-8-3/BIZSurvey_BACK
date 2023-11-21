package com.bipa.bizsurvey.domain.community.service;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.domain.QSurveyPost;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.CreateSurveyPostRequest;
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
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@AllArgsConstructor
public class SurveyPostService {

    private final SurveyPostRepository surveyPostRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SurveyService surveyService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
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
                 .where(p.delFlag.eq(false))
                 .fetchOne();

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
                 .commentList(commentService.getCommentList(postId))
                 .build();

         return surveyPostResponse;

    }
//.innerJoin(sp).on(p.eq(sp.post))




}
