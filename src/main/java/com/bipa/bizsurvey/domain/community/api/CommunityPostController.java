package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.application.CommentService;
import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.dto.request.post.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.application.PostService;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostTableResponse;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostTitleResponse;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
import com.bipa.bizsurvey.domain.user.application.UserService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.global.common.CustomPageImpl;
import com.bipa.bizsurvey.global.common.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityPostController {
    //

    private final PostService postService;
    private final RedisService redisService;

     //CREATE
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostRequest createPostRequest,
                                        @AuthenticationPrincipal LoginUser loginUser){
        
        return ResponseEntity.ok().body(postService.createPost(loginUser.getId(), createPostRequest)); // TODO : 생성된 게시물 ID 리턴
    }

     //게시물 전체 조회
    @GetMapping("")
    public ResponseEntity<?> getPostList(@PageableDefault(size = 15) Pageable pageable){
       return ResponseEntity.ok().body(postService.getPostList(pageable));
    }

    // 게시물 상세 조회
    // /community/showPost/{postId}
    @GetMapping("/showPost/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId){
        return ResponseEntity.ok().body(postService.getPost(postId));
    }

    // 게시물 검색
    @GetMapping ("/search")
    public ResponseEntity<?> searchPost(@RequestParam String keyword,
                                        @PageableDefault(size = 15)Pageable pageable
                                        ){

        return ResponseEntity.ok().body(postService.searchPost(keyword, pageable));
    }

    // update
    @PatchMapping("/updatePost/{postId}")
    public ResponseEntity<?> updatePost(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Long postId,
                                        @Valid @RequestBody UpdatePostRequest updatePostRequest){

        return ResponseEntity.ok().body(postService.updatePost(loginUser.getId(), postId, updatePostRequest));
    }

    // delete
    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Long postId){
        postService.deletePost(loginUser.getId(), postId);
        return ResponseEntity.ok().body("게시물이 삭제되었습니다.");
    }

    // searchTitle
    @PostMapping("/findPostTitle")
    public ResponseEntity<?> findPostTitle(@RequestBody SearchPostRequest searchPostRequest){

        if(searchPostRequest.getKeyword().equals("") || searchPostRequest.getKeyword().equals(" ")){
            return ResponseEntity.ok().body("");
        }


        List<String> titles = redisService.getData("searchTitles", ArrayList.class)
                .orElseThrow( () -> new PostException(PostExceptionType.NO_RESULT));
        if(titles.size() == 0){
            return ResponseEntity.ok().body("");
        }


        List<PostTitleResponse> answerList = new ArrayList<>();
        for (String title : titles) {
            if(title != null && title.contains(searchPostRequest.getKeyword())){
                if(answerList.size() >= 10) {
                    break;
                }
                answerList.add(PostTitleResponse.builder()
                                .result(title)
                                .build()
                );
            }
        }



        return ResponseEntity.ok().body(answerList);
    }
}
