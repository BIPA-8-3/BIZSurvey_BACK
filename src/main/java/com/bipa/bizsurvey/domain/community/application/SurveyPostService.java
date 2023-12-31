package com.bipa.bizsurvey.domain.community.application;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.domain.QSurveyPost;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.CreateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.UpdateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import com.bipa.bizsurvey.domain.community.dto.response.surveyPost.SurveyPostCardResponse;
import com.bipa.bizsurvey.domain.community.dto.response.surveyPost.SurveyPostResponse;
import com.bipa.bizsurvey.domain.community.enums.AccessType;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.application.SurveyCommunityService;
import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.global.common.CustomPageImpl;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Transactional
@AllArgsConstructor
public class SurveyPostService {

    //

    private final SurveyPostRepository surveyPostRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SurveyService surveyService;
    private final SurveyCommunityService surveyCommunityService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final PostImageService postImageService;
    private final QPost p = QPost.post;
    private final QSurveyPost sp = QSurveyPost.surveyPost;
    @CacheEvict(value = "postSurveyListCache", allEntries = true)
    public Long createSurveyPost(Long userId, CreateSurveyPostRequest createSurveyPostRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Post post = Post.builder()
                .user(user)
                .postType(PostType.SURVEY)
                .title(createSurveyPostRequest.getTitle())
                .content(createSurveyPostRequest.getContent())
                .build();
        Post save = postRepository.save(post);

        if(createSurveyPostRequest.getImageUrlList() != null){
            postImageService.createPostImages(save.getId(),
                    createSurveyPostRequest.getImageUrlList());
        }

        Survey survey = surveyService.findSurvey(createSurveyPostRequest.getSurveyId());

        SurveyPost surveyPost = SurveyPost.builder()
                .startDateTime(createSurveyPostRequest.getStartDateTime())
                .endDateTime(createSurveyPostRequest.getEndDateTime())
                .post(save)
                .survey(survey)
                .thumbImgUrl(createSurveyPostRequest.getThumbImageUrl())
                .build();

        createScore(surveyPost);

        surveyPostRepository.save(surveyPost);

        return save.getId();
    }

    // 상세 조회
    public SurveyPostResponse getSurveyPost(Long postId){
         Tuple tuple = jpaQueryFactory // TODO : Optional.ofNullable 예외처리 필요
                 .select(
                         p.id,
                         p.title,
                         p.content,
                         p.count,
                         p.user.nickname,
                         p.regDate,
                         p.reported,
                         sp.survey.id,
                         sp.thumbImgUrl,
                         sp.startDateTime,
                         sp.endDateTime,
                         sp.survey.id

                 )
                 .from(p)
                 .innerJoin(sp).on(p.eq(sp.post))
                 .where(p.id.eq(postId))
                 .where(p.delFlag.eq(false))
                 // (신고 당한 게시물도 리턴
                 .fetchOne();

         Post post = postService.findPost(postId);
         post.addCount(); // 조회수 증가

        List<CommentResponse> commentList = commentService.getCommentList(postId);


        return SurveyPostResponse.builder()
                .postId(tuple.get(p.id))
                .title(tuple.get(p.title))
                .content(tuple.get(p.content))
                .count(tuple.get(p.count))
                .createDate(tuple.get(p.regDate).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .nickname(tuple.get(p.user.nickname))
                .startDateTime(tuple.get(sp.startDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .endDateTime(tuple.get(sp.endDateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .commentList(commentList)
                .commentSize(commentList.size())
                .imageResponseList(postImageService.getImageList(postId))
                .canAccess(checkAccess(tuple.get(sp.startDateTime), tuple.get(sp.endDateTime)))
                .reported(isReported(tuple.get(p.reported)))
                .surveyId(tuple.get(sp.survey.id))
                .thumbImageUrl(tuple.get(sp.thumbImgUrl))
                .profile(post.getUser().getProfile())
                .build();
    }

    // 전체 조회
    // 맥스 멤버 추가
    @Cacheable(value = "postSurveyListCache", key = "#pageable.pageNumber", cacheManager = "jdkCacheManager")
    public CustomPageImpl<?> getSurveyPostList(Pageable pageable){

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
                .where(p.reported.eq(false))
                .orderBy(sp.score.desc())
                .orderBy(p.count.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<SurveyPostCardResponse> results = new ArrayList<>();

        for(Tuple tuple : tupleList){

            // 엔티티 조회
            SurveyPost surveyPost = jpaQueryFactory
                    .selectFrom(sp)
                    .where(sp.post.id.eq(tuple.get(p.id)))
                    .fetchOne();

            // 점수 생성
            createScore(surveyPost);

            SurveyPostCardResponse surveyPostResponse = SurveyPostCardResponse.builder()
                    .postId(tuple.get(p.id))
                    .surveyPostId(tuple.get(sp.id))
                    .title(tuple.get(p.title))
                    .content(tuple.get(p.content))
                    .nickname(tuple.get(p.user.nickname))
                    .thumbImageUrl(tuple.get(sp.thumbImgUrl))
                    .build();

            results.add(surveyPostResponse);
        }
        return new CustomPageImpl<>(results, pageable.getPageNumber(), pageable.getPageSize(), totalCount);
    }



    // 검색
    public Page<?> searchSurveyPost(String keyword, Pageable pageable){
        long totalCount = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.SURVEY))
                .where(p.content.like("%" + keyword + "%")
                        .or(p.title.like("%" + keyword + "%")))
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
                .innerJoin(sp).on(p.eq(sp.post))
                .where(p.postType.eq(PostType.SURVEY))
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.content.like("%" + keyword + "%")
                        .or(p.title.like("%" + keyword + "%")))
                .orderBy(sp.score.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        List<SurveyPostCardResponse> results = new ArrayList<>();

        for(Tuple tuple : tupleList){

            // 엔티티 조회
            SurveyPost surveyPost = jpaQueryFactory
                    .selectFrom(sp)
                    .where(sp.post.id.eq(tuple.get(p.id)))
                    .fetchOne();

            // 점수 생성
            createScore(surveyPost);

            SurveyPostCardResponse surveyPostResponse = SurveyPostCardResponse.builder()
                    .postId(tuple.get(p.id))
                    .title(tuple.get(p.title))
                    .content(tuple.get(p.content))
                    .count(tuple.get(p.count))
                    .nickname(tuple.get(p.user.nickname))
                    .commentSize(commentService.getCommentList(tuple.get(p.id)).size())
                    .participateCount(surveyCommunityService.getParticipants(tuple.get(sp.id)))
                    .canAccess(checkAccess(tuple.get(sp.id)))
                    .thumbImageUrl(tuple.get(sp.thumbImgUrl))
                    .profile(surveyPost.getPost().getUser().getProfile())
                    .build();

            results.add(surveyPostResponse);
        }
        return new PageImpl<>(results, pageable, totalCount);
    }


    @CacheEvict(value = "postSurveyListCache", allEntries = true)
    public void updateSurveyPost(Long userId, Long postId, UpdateSurveyPostRequest updateSurveyPostRequest){
        postService.checkPermission(userId, postId);
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyService.findSurvey(updateSurveyPostRequest.getSurveyId());
        surveyPost.updateSurveyPost(updateSurveyPostRequest, survey);
        surveyPostRepository.save(surveyPost);
    }
    @CacheEvict(value = "postSurveyListCache", allEntries = true)
    public void deleteSurveyPost(Long userId, Long postId){
        postService.checkPermission(userId, postId);
        Post post = postService.findPost(postId);
        post.setDelFlag(true);
    }

    public String checkAccess(LocalDateTime start, LocalDateTime close){
        LocalDateTime localDateTime = LocalDateTime.now();
        if(localDateTime.isBefore(start)){
            return AccessType.CAN_NOT_START.getIsAccess();
        }else if(localDateTime.isAfter(close)){
            return AccessType.CLOSED.getIsAccess();
        }else {
            return AccessType.CAN_START.getIsAccess();
        }
    }

    public String checkAccess(Long surveyPostId){
        SurveyPost surveyPost = surveyPostRepository.findById(surveyPostId).get();
        LocalDateTime localDateTime = LocalDateTime.now();
        if(localDateTime.isBefore(surveyPost.getStartDateTime())){
            return AccessType.CAN_NOT_START.getIsAccess();
        }else if(localDateTime.isAfter(surveyPost.getEndDateTime())){
            return AccessType.CLOSED.getIsAccess();
        }else {
            return AccessType.CAN_START.getIsAccess();
        }
    }

    private void createScore(SurveyPost surveyPost){
        LocalDateTime localDateTime = LocalDateTime.now();
        if(localDateTime.isBefore(surveyPost.getStartDateTime())){ // 시작 전
            surveyPost.addScore(50); // 시작 전 50점
        }else if(localDateTime.isAfter(surveyPost.getEndDateTime())){
            surveyPost.addScore(0); // 종료 0점
        }else {
            surveyPost.addScore(100); // 서비스 중 : 100점
        }
    }

    public List<String> findSurveyPostTitle(){

        Set<String> set = new HashSet<>();

        List<Tuple> tuples = jpaQueryFactory
                .selectDistinct(p.title, p.count)
                .from(p)
                .where(p.postType.eq(PostType.SURVEY)) // 검색 자동완성이 COMMUNITY 랑 S-COMMUNITY 랑 다르다
                .where(p.delFlag.eq(false).and(p.reported.eq(false)))
                .orderBy(p.count.desc())
                .limit(50)
                .fetch();

        for (Tuple tuple : tuples) {
            set.add(tuple.get(p.title));
        }

        return new ArrayList<String>(set);
    }


    private int isReported(Boolean check){
        if(check == false){
            return 0;
        }else{
            return 1;
        }
    }

    public SurveyPost findSurveyPost(Long id){
        return surveyPostRepository.findById(id).get();
    }




//    private OrderSpecifier<?> sortByField(String filedName){
//
//        Order order = Order.DESC;
//
//        if(Objects.isNull(filedName)){
//            return new OrderSpecifier<>(order, p.id);
//        }
//
//        if(filedName.equals("count")){
//            return new OrderSpecifier<>(order, p.count);
//        }
//
//        if(filedName.equals("regDate")){
//            return new OrderSpecifier<>(order, p.regDate);
//        }
//
//        return OrderByNull.getDefault();
//    }


}
