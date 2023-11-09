package com.bipa.bizsurvey.domain.community.service;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.dto.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.response.PostResponse;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 커뮤니티 게시물 제작
    public void createPost(Long userId, CreatePostRequest createPostRequest){
        User user = userRepository.findById(userId).get(); // TODO : Exception Handling
        Post post = Post.toEntity(user, PostType.COMMUNITY, createPostRequest);
        postRepository.save(post);
    }

    // 게시물 조회
    // page -> 시작 페이지(0)
    // size -> 한 페이지에 표시해야 할 게시물의 개수
    // sortBy -> 소팅 컬럼 기준
    public Page<?> getPostList(int page, int size, String sortBy){
        Sort sort = Sort.by(Sort.Order.desc(sortBy));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> postPages = postRepository.findAll(pageable);

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





}
