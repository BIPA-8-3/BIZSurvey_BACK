package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyResultService;
import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.dto.request.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.request.UpdateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.response.PersonalResultResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResultResponse;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyResultService surveyResultService;

    //설문지 상세 조회
    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyInWorkspaceResponse> getSurvey(@PathVariable Long surveyId,
                                                               @AuthenticationPrincipal LoginUser loginUser){
        return ResponseEntity.ok().body(surveyService.getSurvey(surveyId, loginUser));
    }

    //설문지 등록
    @PostMapping("/{workspaceId}")
    public ResponseEntity<String> createSurvey(@RequestBody @Valid CreateSurveyRequest createSurveyRequest,
                                               @AuthenticationPrincipal LoginUser loginUser,
                                               @PathVariable Long workspaceId) {
        surveyService.createSurvey(createSurveyRequest, loginUser, workspaceId);
        return ResponseEntity.ok().body("등록이 완료되었습니다.");

    }

    //설문지 수정
    @PutMapping
    public ResponseEntity<String> updateSurvey(@RequestBody @Valid UpdateSurveyRequest updateSurveyRequest,
                                               @AuthenticationPrincipal LoginUser loginUser) {
        surveyService.updateSurvey( updateSurveyRequest, loginUser);
        return ResponseEntity.ok().body("수정이 완료되었습니다.");
    }

    //설문지 삭제
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long surveyId,
                                               @AuthenticationPrincipal LoginUser loginUser){
        surveyService.deleteSurvey(surveyId, loginUser);
        return ResponseEntity.ok().body("삭제가 완료되었습니다.");
    }

    //설문지 통계
    @GetMapping("/result/{postId}")
    public ResponseEntity<SurveyResultResponse> getSurveyResultInPost(@PathVariable Long postId){
        return ResponseEntity.ok().body(surveyResultService.getSurveyResultInPost(postId));
    }

    //설문지 개별 통계
    @GetMapping("/result/{surveyPostId}/{nickname}")
    public ResponseEntity<PersonalResultResponse> getPersonalResultInPost(@PathVariable Long surveyPostId, @PathVariable String nickname){
        return ResponseEntity.ok().body(surveyResultService.getPersonalResultInPost(surveyPostId, nickname));
    }




}
