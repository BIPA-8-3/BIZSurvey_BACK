package com.bipa.bizsurvey.domain.community.service;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.dto.request.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.response.PostResponse;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.criterion.Projection;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 커뮤니티 게시물 제작
    public void createPost(Long userId, CreatePostRequest createPostRequest){
        User user = userRepository.findById(userId).get(); // TODO : Exception Handling
        Post post = Post.toEntity(user, PostType.COMMUNITY, createPostRequest);
        postRepository.save(post);
    }

    // 게시물 전체 조회
    // page -> 시작 페이지(0)
    // size -> 한 페이지에 표시해야 할 게시물의 개수
    // sortBy -> 소팅 컬럼 기준
    public Page<?> getPostList(int page, int size, String sortBy){
        Sort sort = Sort.by(Sort.Order.desc(sortBy));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> postPages = postRepository.findByDelFlagIsFalse(pageable); // TODO : 기능 잘 구현되는지 확인

        return postPages.map(
                p -> PostResponse.builder()
                        .postId(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .count(p.getCount())
                        .nickname(p.getUser().getNickname())
                        .build()
        );

    }
     // 게시물 검색
    // TODO : 기능 잘 구현되는지 확인
    public Page<?> searchPost(SearchPostRequest searchPostRequest, Pageable pageable){
        QPost p = new QPost("p");

        List<Post> postList = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
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
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .count(post.getCount())
                .nickname(post.getUser().getNickname())
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


    private Post findPost(Long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new PostException(PostExceptionType.NON_EXIST_POST)
        );
    }

    private void checkPermission(Long userId, Post post) {
            if(!Objects.equals(userId, post.getUser().getId())){
                throw new PostException(UserExceptionType.NO_PERMISSION);
            }
    }

    private void checkAvailable(Post post){
        if(post.getDelFlag()){
            throw new PostException(PostExceptionType.ALREADY_DELETED);
        }
    }





}
