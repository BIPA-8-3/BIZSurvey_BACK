package com.bipa.bizsurvey.domain.community.service;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.dto.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class PostServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @BeforeEach
    public void beforeEach(){
        User user = User.builder()
                .email("dlathf3210@naver.com")
                .name("lim")
                .nickname("saul")
                .gender(Gender.MALE)
                .birthdate("19971002")
                .build();

        userRepository.save(user);
    }


    @Test
    @Rollback(value = false)
    @DisplayName("게시물 생성 테스트 코드")
    public void createPostTest(){
        User user = userRepository.findById(1L).get();
        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setTitle("title");
        createPostRequest.setContent("content");

        postService.createPost(user.getId(), createPostRequest);
    }

}