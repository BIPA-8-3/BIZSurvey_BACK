package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.dto.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.UpdateSurveyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

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


}
