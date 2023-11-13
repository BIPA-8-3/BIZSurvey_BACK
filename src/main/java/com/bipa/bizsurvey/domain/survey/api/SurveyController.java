package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyResultService;
import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.dto.survey.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.survey.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.survey.UpdateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.surveyresult.PersonalResultResponse;
import com.bipa.bizsurvey.domain.survey.dto.surveyresult.SurveyResultResponse;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.AllArgsConstructor;
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

    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyInWorkspaceResponse> getSurvey(@PathVariable Long surveyId,
                                                               @AuthenticationPrincipal LoginUser loginUser){

        return ResponseEntity.ok().body(surveyService.getSurvey(surveyId, loginUser));
    }

    @PostMapping
    public ResponseEntity<String> createSurvey(@RequestBody @Valid CreateSurveyRequest createSurveyRequest,
                                               @AuthenticationPrincipal LoginUser loginUser) {

        surveyService.createSurvey(createSurveyRequest, loginUser);

        return ResponseEntity.ok().body("등록이 완료되었습니다.");

    }

    @PutMapping("/{surveyId}")
    public ResponseEntity<String> updateSurvey(@PathVariable Long surveyId,
                                               @RequestBody @Valid UpdateSurveyRequest updateSurveyRequest,
                                               @AuthenticationPrincipal LoginUser loginUser) {

        surveyService.updateSurvey(surveyId, updateSurveyRequest, loginUser);

        return ResponseEntity.ok().body("수정이 완료되었습니다.");
    }

    //delete
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long surveyId,
                                               @AuthenticationPrincipal LoginUser loginUser){

        surveyService.deleteSurvey(surveyId, loginUser);

        return ResponseEntity.ok().body("삭제가 완료되었습니다.");
    }


    @GetMapping("/result/{postId}")
    public ResponseEntity<SurveyResultResponse> getSurveyResultInPost(@PathVariable Long postId){

        return ResponseEntity.ok().body(surveyResultService.getSurveyResultInPost(postId));
    }

    @GetMapping("/result/{surveyPostId}/{nickname}")
    public ResponseEntity<PersonalResultResponse> getPersonalResultInPost(@PathVariable Long surveyPostId, @PathVariable String nickname){

        return ResponseEntity.ok().body(surveyResultService.getPersonalResultInPost(surveyPostId, nickname));
    }




}
