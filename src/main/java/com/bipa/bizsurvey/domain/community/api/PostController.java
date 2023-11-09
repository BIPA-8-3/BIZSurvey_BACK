package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostController {

    private final PostService postService;

    //READ


     //CREATE
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostRequest createPostRequest,
                                        @AuthenticationPrincipal Long userId){

        Long testId = 1L; // TODO : @AuthenticationPrincipal 추 후 사용

        postService.createPost(testId, createPostRequest);
        return ResponseEntity.ok().body("게시물 등록이 완료되었습니다."); // 200 OK
    }


    @GetMapping("/search")
    public ResponseEntity<?> getPostList(@RequestParam int page,
                                         @RequestParam int size,
                                         @RequestParam String sortBy
                                         ){
        return ResponseEntity.ok().body(postService.getPostList(page, size, sortBy));
    }

}
