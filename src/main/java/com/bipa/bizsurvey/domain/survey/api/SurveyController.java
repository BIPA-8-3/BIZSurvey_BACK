package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyResultService;
import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.dto.survey.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.survey.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.survey.UpdateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.surveyresult.SurveyResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyResultService surveyResultService;

    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyInWorkspaceResponse> getSurvey(@PathVariable Long surveyId){

        return ResponseEntity.ok().body(surveyService.getSurvey(surveyId));

    }

    @PostMapping
    public ResponseEntity<String> createSurvey(@RequestBody @Valid CreateSurveyRequest createSurveyRequest) {

        surveyService.createSurvey(createSurveyRequest);

        return ResponseEntity.ok().body("등록이 완료되었습니다.");

    }

    @PutMapping("/{surveyId}")
    public ResponseEntity<String> updateSurvey(@PathVariable Long surveyId, @RequestBody @Valid UpdateSurveyRequest updateSurveyRequest) {

        surveyService.updateSurvey(updateSurveyRequest);

        return ResponseEntity.ok().body("수정이 완료되었습니다.");
    }

    //delete


    @GetMapping("/result/{postId}")
    public ResponseEntity<SurveyResultResponse> getSurveyResultInPost(@PathVariable Long postId){

        return ResponseEntity.ok().body(surveyResultService.getSurveyResultInPost(postId));

    }




}
