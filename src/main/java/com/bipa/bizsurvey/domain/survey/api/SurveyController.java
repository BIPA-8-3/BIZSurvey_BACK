package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.dto.SurveyInWorkspaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createSurvey() {



        return null;


    }

}
