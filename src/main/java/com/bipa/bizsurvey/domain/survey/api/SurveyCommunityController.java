package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyCommunityService;
import com.bipa.bizsurvey.domain.survey.dto.request.ParticipateSurveyRequest;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/s-community/survey")
@RequiredArgsConstructor
public class SurveyCommunityController {

    private final SurveyCommunityService surveyCommunityService;

    // 설문 참여 페이지 조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getSurvey(@PathVariable Long postId,
                                       @AuthenticationPrincipal LoginUser loginUser){
        return ResponseEntity.ok().body(surveyCommunityService.getSurvey(postId, loginUser));
    }

    // 시용자 설문 참여 저장
    @PostMapping("/{postId}")
    public ResponseEntity<?> participateSurvey(@RequestBody List<ParticipateSurveyRequest> participateSurvey,
                                               @PathVariable Long postId,
                                               @AuthenticationPrincipal LoginUser loginUser){
        surveyCommunityService.participateSurvey(participateSurvey, postId, loginUser);
        return ResponseEntity.ok().body("설문 참여가 완료되었습니다.");
    }

    // 사용자가 작성한 설문지 목록(게시글 등록 시)
    @GetMapping("/list")
    public ResponseEntity<?> getSurveyList(@AuthenticationPrincipal LoginUser loginUser){

        return ResponseEntity.ok().body(surveyCommunityService.getSurveyList(loginUser));
    }


}
