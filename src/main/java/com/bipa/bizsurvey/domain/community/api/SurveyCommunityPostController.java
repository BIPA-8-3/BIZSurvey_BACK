package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.request.post.SearchPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.CreateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.UpdateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.application.SurveyPostService;
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
@RequestMapping("/s-community")
public class SurveyCommunityPostController {

    private final SurveyPostService surveyPostService;

    @PostMapping("/createPost")
    public ResponseEntity<?> createSurveyPost(@AuthenticationPrincipal LoginUser loginUser,
                                              @Valid @RequestBody CreateSurveyPostRequest createSurveyPostRequest
                                              ){
        surveyPostService.createSurveyPost(loginUser.getId(), createSurveyPostRequest);
        return ResponseEntity.ok().body("설문 게시물이 생성되었습니다.");
    }

    // 전체 조회
    @GetMapping("")
    public ResponseEntity<?> getSurveyPostList(@PageableDefault(size = 8)Pageable pageable,
                                               @RequestParam(required = false) String fieldName
                                               ){
        return ResponseEntity.ok().body(surveyPostService.getSurveyPostList(pageable, fieldName));
    }

    ///sCommunity/showSPost/{post_id}
    @GetMapping("/showPost/{postId}")
    public ResponseEntity<?> showSurveyPost(@PathVariable Long postId){
        return ResponseEntity.ok().body(surveyPostService.getSurveyPost(postId));
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchSurveyPost(@Valid @RequestBody SearchPostRequest searchPostRequest,
                                              @PageableDefault(size = 10)Pageable pageable
                                              ){
        return ResponseEntity.ok().body(surveyPostService.searchSurveyPost(searchPostRequest,pageable));
    }

    @PatchMapping("/updateSurveyPost/{postId}")
    public ResponseEntity<?> updateSurveyPost(@AuthenticationPrincipal LoginUser loginUser,
                                              @PathVariable Long postId,
                                              @Valid @RequestBody UpdateSurveyPostRequest updateSurveyPostRequest
                                              ){
        surveyPostService.updateSurveyPost(loginUser.getId(), postId, updateSurveyPostRequest);
        return ResponseEntity.ok().body("게시물이 수정되었습니다.");
    }


}
