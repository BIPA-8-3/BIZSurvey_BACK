package com.bipa.bizsurvey.domain.community.api;

import com.bipa.bizsurvey.domain.community.dto.request.surveyPost.CreateSurveyPostRequest;
import com.bipa.bizsurvey.domain.community.service.SurveyPostService;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
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

    ///sCommunity/showSPost/{post_id}
    @GetMapping("/showPost/{postId}")
    public ResponseEntity<?> showSurveyPost(@PathVariable Long postId){
        return ResponseEntity.ok().body(surveyPostService.getSurveyPost(postId));
    }
}
