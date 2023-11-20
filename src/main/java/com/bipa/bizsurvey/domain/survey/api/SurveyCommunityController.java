package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyCommunityService;
import com.bipa.bizsurvey.domain.survey.dto.request.ParticipateSurveyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sCommunity/survey")
@RequiredArgsConstructor
public class SurveyCommunityController {

    private final SurveyCommunityService surveyCommunityService;

    // 설문 참여 페이지 조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getSurvey(@PathVariable Long postId){
        return ResponseEntity.ok().body(surveyCommunityService.getSurvey(postId));
    }

    // 시용자 설문 참여 저장
    @PostMapping("/{postId}")
    public ResponseEntity<?> participateSurvey(@RequestBody List<ParticipateSurveyRequest> participateSurvey,
                                               @PathVariable Long postId){
        surveyCommunityService.participateSurvey(participateSurvey, postId);
        return ResponseEntity.ok().body("설문 참여가 완료되었습니다.");
    }





}
