package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.CreatePostRequest;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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
    public void testCreatePost() throws Exception {

        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setTitle("새로운 게시물");
        createPostRequest.setContent("게시물 내용");


        mockMvc.perform(MockMvcRequestBuilders.post("/community/createPost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("게시물 등록이 완료되었습니다."));
    }


}