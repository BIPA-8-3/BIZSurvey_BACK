package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.application.CommentService;
import com.bipa.bizsurvey.domain.community.dto.request.post.CreatePostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.community.application.PostService;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostTableResponse;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostTitleResponse;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostExceptionType;
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

    private final PostService postService;
    private final CommentService commentService;
    private final RedisService redisService;

     //CREATE
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostRequest createPostRequest,
                                        @AuthenticationPrincipal LoginUser loginUser){
        
        return ResponseEntity.ok().body(postService.createPost(1L, createPostRequest)); // TODO : 생성된 게시물 ID 리턴
    }

     //게시물 전체 조회
    @GetMapping("")
    public ResponseEntity<?> getPostList(@PageableDefault(size = 15) Pageable pageable){
        CustomPageImpl<?> postPage = postService.getPostList(pageable);
        List<?> content = postPage.getContent();
        for (Object o : content) {
            PostTableResponse postTableResponse = (PostTableResponse) o;
            postTableResponse.setCommentSize(commentService.getCommentList(postTableResponse.getPostId()).size());
            postTableResponse.setCount(postService.getPostCount(postTableResponse.getPostId()));
        }

        return ResponseEntity.ok().body(postPage); // 200 OK
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
