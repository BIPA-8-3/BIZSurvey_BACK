package com.bipa.bizsurvey.domain.community.application;

import com.bipa.bizsurvey.domain.community.domain.Comment;
import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.dto.request.post.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostResponse;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostTableResponse;
import com.bipa.bizsurvey.domain.community.enums.CreateType;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
import com.bipa.bizsurvey.domain.community.repository.CommentRepository;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.global.common.sorting.OrderByNull;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final CommentService commentService;
    private final PostImageService postImageService;
    public QPost p = QPost.post;

    // 커뮤니티 게시물 제작
    public void createPost(Long userId, CreatePostRequest createPostRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Post post = Post.toEntity(user, PostType.COMMUNITY, createPostRequest);

        if(createPostRequest.getVoteId() != null){
            post.setVoteId(createPostRequest.getVoteId());
        }

        Post save = postRepository.save(post);

        if(createPostRequest.getImageUrlList() != null){
            postImageService.createPostImages(save.getId() , createPostRequest.getImageUrlList());
        }
    }

    // 게시물 전체 조회
    // page -> 시작 페이지(0)
    // size -> 한 페이지에 표시해야 할 게시물의 개수
    // sortBy -> 소팅 컬럼 기준

    // TODO : 신고된 게시물 띄우지 않기로(추가해야함)
    // TODO : QueryDSL 로 업데이트
    public Page<?> getPostList(Pageable pageable){


        List<Long> postBestIdList = jpaQueryFactory
                .select(p.id)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.COMMUNITY))
                .offset(0)
                .limit(5)
                .orderBy(p.count.desc())
                .fetch();

        long totalCount = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.postType.eq(PostType.COMMUNITY))
                .where(p.delFlag.eq(false))
                .stream().count();

        List<Post> postList = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.COMMUNITY))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(p.regDate.desc())
                .fetch();

        List<PostTableResponse> result = new ArrayList<>();

        for(Post post: postList){

            PostTableResponse postTableResponse = PostTableResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .count(post.getCount())
                    .nickname(post.getUser().getNickname())
                    .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .commentSize(commentService.getCommentList(post.getId()).size())
                    .voteId(post.getVoteId())
                    .createType(checkCreateType(post))
                    .build();

            result.add(postTableResponse);
        }

        return new PageImpl<>(checkIsBest(postBestIdList, result), pageable, totalCount);
    }





    // TODO : 신고된 게시물 띄우지 않기로(추가됨)
    public Page<?> searchPost(String keyword, Pageable pageable){

        long totalCount = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.COMMUNITY))
                .where(p.content.like("%" + keyword + "%")
                        .or(p.title.like("%" + keyword + "%")))
                .stream().count();

        List<Post> postList = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.COMMUNITY))
                .where(p.content.like("%" + keyword + "%")
                        .or(p.title.like("%" + keyword + "%")))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(p.count.desc())
                .fetch();

        List<PostResponse> result = new ArrayList<>();

        for(Post post: postList){
            PostResponse postResponse = PostResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .count(post.getCount())
                    .nickname(post.getUser().getNickname())
                    .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .build();
            result.add(postResponse);
        }



        return new PageImpl<>(result, pageable, totalCount);
    }



    // 게시물 상세 조회
    // /community/updatePost/{post_id}
    public PostResponse getPost(Long postId){
        Post post = findPost(postId);
        checkAvailable(post);
        post.addCount(); // 조회수 증가

        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .count(post.getCount())
                .nickname(post.getUser().getNickname())
                .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) // TODO : 이 양식으로 전부 추가
                .commentList(commentService.getCommentList(postId))
                .imageResponseList(postImageService.getImageList(postId))
                .voteId(post.getVoteId())
                .commentSize(commentService.getCommentList(postId).size())
                .build();
    }


    // 게시물 수정
    // /community/updatePost/{post_id}
    public void updatePost(Long userId, Long postId, UpdatePostRequest updatePostRequest){

        if(updatePostRequest.getAddImgUrlList() != null){
            postImageService.createPostImages(postId, updatePostRequest.getAddImgUrlList());
        }

        if(updatePostRequest.getDeleteImgUrlList() != null){
            postImageService.deletePostImages(postId, updatePostRequest.getDeleteImgUrlList());
        }


        Post post = checkPermission(userId, postId);
        post.updatePost(updatePostRequest);
        postRepository.save(post);
    }

    // 게시물 삭제
    // /community/deletePost/{postId}
    public void deletePost(Long userId, Long postId){
        Post post = checkPermission(userId, postId);
        checkAvailable(post);
        post.updateDelFlag();
    }


    public Post findPost(Long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new PostException(PostExceptionType.NON_EXIST_POST)
        );
    }

    // 조회수 기준 상위 50개 제목을를 찾아오는 post 메소드
    public List<String> findPostTitle(){

        Set<String> set = new HashSet<>();

        List<Tuple> tuples = jpaQueryFactory
                .selectDistinct(p.title, p.count)
                .from(p)
                .where(p.postType.eq(PostType.COMMUNITY)) // 검색 자동완성이 COMMUNITY 랑 S-COMMUNITY 랑 다르다
                .where(p.delFlag.eq(false).and(p.reported.eq(false)))
                .orderBy(p.count.desc())
                .limit(50)
                .fetch();

        for (Tuple tuple : tuples) {
            set.add(tuple.get(p.title));
        }

        return new ArrayList<String>(set);
    }

    public String checkCreateType(Post post){
        LocalDateTime postTime = post.getRegDate();
        LocalDateTime currentTime = LocalDateTime.now();

        if(isSameDay(postTime, currentTime)){
            return CreateType.TODAY.getValue();
        }else{
            return CreateType.BEFORE.getValue();
        }
    }

    public List<PostTableResponse> checkIsBest(List<Long> postIdList, List<PostTableResponse> postTableResponses){
        for (PostTableResponse postTableResponse : postTableResponses) {
            if (postIdList.contains(postTableResponse.getPostId())) {
                postTableResponse.setIsBest("best");
            }
        }

        return postTableResponses;
    }


    private boolean isSameDay(LocalDateTime dateTime1, LocalDateTime dateTime2){
        LocalDate date1 = dateTime1.toLocalDate();
        LocalDate date2 = dateTime2.toLocalDate();
        return date1.isEqual(date2);
    }


    public Post checkPermission(Long userId, Long postId) {

        Post post = findPost(postId);

        if(!Objects.equals(userId, post.getUser().getId())){
            throw new UserException(UserExceptionType.NO_PERMISSION);
        }

        return post;
    }

    public void checkAvailable(Post post){
        if(post.getDelFlag()){
            throw new PostException(PostExceptionType.ALREADY_DELETED);
        }
    }

    // 동적 정렬 기능 보류

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
