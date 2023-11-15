package com.bipa.bizsurvey.domain.community.service;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.dto.request.post.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostResponse;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.global.common.sorting.OrderByNull;
import com.bipa.bizsurvey.global.common.sorting.SortingRequest;
import com.bipa.bizsurvey.global.common.sorting.SortingStandard;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final CommentService commentService;
    public QPost p = new QPost("p");

    // 커뮤니티 게시물 제작
    public void createPost(Long userId, CreatePostRequest createPostRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Post post = Post.toEntity(user, PostType.COMMUNITY, createPostRequest);
        postRepository.save(post);
    }

    // 게시물 전체 조회
    // page -> 시작 페이지(0)
    // size -> 한 페이지에 표시해야 할 게시물의 개수
    // sortBy -> 소팅 컬럼 기준

    // TODO : 신고된 게시물 띄우지 않기로(추가해야함)
    // TODO : QueryDSL 로 업데이트
    public Page<?> getPostList(Pageable pageable, String fieldName){


        List<Post> postList = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.COMMUNITY))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortByField(fieldName))
                .fetch();

        List<PostResponse> result = new ArrayList<>();

        for(Post post: postList){
            PostResponse postResponse = PostResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .count(post.getCount())
                    .nickname(post.getUser().getNickname())
                    .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
                    .build();
            result.add(postResponse);
        }



        return new PageImpl<>(result, pageable, result.size());
    }





    // TODO : 신고된 게시물 띄우지 않기로(추가됨)
    public Page<?> searchPost(SearchPostRequest searchPostRequest, Pageable pageable){
        QPost p = new QPost("p");

        List<Post> postList = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false)) 
                .where(p.postType.eq(PostType.COMMUNITY))
                .where(p.content.like("%" + searchPostRequest.getKeyword() + "%")
                        .or(p.title.like("%" + searchPostRequest.getKeyword() + "%")))
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
                    .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
                    .build();
            result.add(postResponse);
        }



        return new PageImpl<>(result, pageable, result.size());
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
                .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm"))) // TODO : 이 양식으로 전부 추가
                .commentList(commentService.getCommentList(postId))
                .build();
    }


    // 게시물 수정
   // /community/updatePost/{post_id}
    public void updatePost(Long userId, Long postId, UpdatePostRequest updatePostRequest){
        Post post = findPost(postId);
        checkPermission(userId, post);
        post.updatePost(updatePostRequest);
        postRepository.save(post);
    }

    // 게시물 삭제
    // /community/deletePost/{postId}
    public void deletePost(Long userId, Long postId){
        Post post = findPost(postId);
        checkAvailable(post);
        checkPermission(userId, post);
        post.updateDelFlag();
    }


    public Post findPost(Long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new PostException(PostExceptionType.NON_EXIST_POST)
        );
    }

    public void checkPermission(Long userId, Post post) {
            if(!Objects.equals(userId, post.getUser().getId())){
                throw new PostException(UserExceptionType.NO_PERMISSION);
            }
    }

    public void checkAvailable(Post post){
        if(post.getDelFlag()){
            throw new PostException(PostExceptionType.ALREADY_DELETED);
        }
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
