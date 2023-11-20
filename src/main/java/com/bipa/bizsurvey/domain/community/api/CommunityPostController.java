package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.request.post.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.service.PostService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityPostController {

    private final PostService postService;

     //CREATE
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostRequest createPostRequest,
                                        @AuthenticationPrincipal LoginUser loginUser){

        postService.createPost(loginUser.getId(), createPostRequest);
        return ResponseEntity.ok().body("게시물 등록이 완료되었습니다."); // 200 OK
    }

     //게시물 전체 조회
    @GetMapping("")
    public ResponseEntity<?> getPostList(@PageableDefault(size = 10) Pageable pageable,
                                         @RequestParam(required = false) String fieldName
                                         ){

        return ResponseEntity.ok().body(postService.getPostList(pageable, fieldName)); // 200 OK
    }

    // 게시물 상세 조회
    // /community/showPost/{postId}
    @GetMapping("/showPost/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId){
        return ResponseEntity.ok().body(postService.getPost(postId));
    }

    // 게시물 검색
    @PostMapping("/search")
    public ResponseEntity<?> searchPost(@Valid @RequestBody SearchPostRequest searchPostRequest,
                                        @PageableDefault(size = 10)Pageable pageable
                                        ){

        return ResponseEntity.ok().body(postService.searchPost(searchPostRequest, pageable));
    }

    // update
    @PatchMapping("/updatePost/{postId}")
    public ResponseEntity<?> updatePost(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Long postId,
                                        @Valid @RequestBody UpdatePostRequest updatePostRequest){
        postService.updatePost(loginUser.getId(), postId, updatePostRequest);
        return ResponseEntity.ok().body("게시물이 수정되었습니다.");
    }

    // delete
    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Long postId){
        postService.deletePost(loginUser.getId(), postId);
        return ResponseEntity.ok().body("게시물이 삭제되었습니다.");
    }



}
