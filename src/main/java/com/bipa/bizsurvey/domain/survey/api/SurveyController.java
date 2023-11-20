package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.statistics.StatisticsService;
import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.dto.request.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.response.StatisticsResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.request.UpdateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyListResponse;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final StatisticsService statisticsService;

    //설문지 목록 조회
    @GetMapping("/list/{workspaceId}")
    public ResponseEntity<List<SurveyListResponse>> getSurveyList(@PathVariable Long workspaceId,
                                                                  @RequestParam(required = false) String fieldName){
        return ResponseEntity.ok().body(surveyService.getSurveyList(workspaceId, fieldName));
    }



    //설문지 상세 조회
    @GetMapping("/{surveyId}/{workspaceId}")
    public ResponseEntity<SurveyResponse> getSurvey(@PathVariable Long surveyId,
                                                    @PathVariable Long workspaceId,
                                                    @AuthenticationPrincipal LoginUser loginUser){
        return ResponseEntity.ok().body(surveyService.getSurvey(surveyId));
    }

    //설문지 등록
    @PostMapping("/{workspaceId}")
    public ResponseEntity<String> createSurvey(@RequestBody @Valid CreateSurveyRequest createSurveyRequest,
                                               @AuthenticationPrincipal LoginUser loginUser,
                                               @PathVariable Long workspaceId) {
        surveyService.createSurvey(createSurveyRequest, loginUser, workspaceId);
        return ResponseEntity.ok().body("설문지 등록이 완료되었습니다.");
    }

    //설문지 수정
    @PatchMapping("/{workspaceId}")
    public ResponseEntity<String> updateSurvey(@RequestBody @Valid UpdateSurveyRequest updateSurveyRequest,
                                               @AuthenticationPrincipal LoginUser loginUser,
                                               @PathVariable Long workspaceId) {
        surveyService.updateSurvey( updateSurveyRequest, loginUser, workspaceId);
        return ResponseEntity.ok().body("설문지 수정이 완료되었습니다.");
    }

    //설문지 삭제
    @DeleteMapping("/{surveyId}/{workspaceId}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long surveyId,
                                               @PathVariable Long workspaceId,
                                               @AuthenticationPrincipal LoginUser loginUser){
        surveyService.deleteSurvey(surveyId, loginUser, workspaceId);
        return ResponseEntity.ok().body("설문지 삭제가 완료되었습니다.");
    }



    //설문지 게시물 통계
    @GetMapping("/result/{surveyId}/{postId}")
    public ResponseEntity<StatisticsResponse> getSurveyResultOfPost(@PathVariable Long postId,
                                                   @PathVariable Long surveyId,
                                                   @RequestParam String type){
        return ResponseEntity.ok().body(statisticsService.getPostResult(surveyId, postId, type));

    }

}
