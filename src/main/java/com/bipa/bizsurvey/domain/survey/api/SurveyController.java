package com.bipa.bizsurvey.domain.survey.api;


import com.bipa.bizsurvey.domain.survey.application.SurveyService;
import com.bipa.bizsurvey.domain.survey.application.StatisticsService;
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
                                                                  @RequestParam(required = false) String type){
        return ResponseEntity.ok().body(surveyService.getSurveyList(workspaceId, type));
    }



    //설문지 상세 조회
    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyResponse> getSurvey(@PathVariable Long surveyId){
        return ResponseEntity.ok().body(surveyService.getSurvey(surveyId));
    }

    //설문지 등록
    @PostMapping("/{workspaceId}")
    public ResponseEntity<String> createSurvey(@RequestBody @Valid CreateSurveyRequest createSurveyRequest,
                                               @AuthenticationPrincipal LoginUser loginUser,
                                               @PathVariable Long workspaceId) {
        surveyService.createSurvey(createSurveyRequest, workspaceId, loginUser);
        return ResponseEntity.ok().body("설문지 등록이 완료되었습니다.");
    }

    //설문지 수정
    @PatchMapping("/{surveyId}")
    public ResponseEntity<String> updateSurvey(@RequestBody @Valid UpdateSurveyRequest updateSurveyRequest,
                                               @PathVariable Long surveyId) {
        surveyService.updateSurvey(updateSurveyRequest, surveyId);
        return ResponseEntity.ok().body("설문지 수정이 완료되었습니다.");
    }

    //설문지 삭제
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long surveyId){
        surveyService.deleteSurvey(surveyId);
        return ResponseEntity.ok().body("설문지 삭제가 완료되었습니다.");
    }


    // 설문 통계 게시물 리스트
    @GetMapping("/result/postList/{surveyId}")
    public ResponseEntity<?> getSurveyPostList(@PathVariable Long surveyId){

        return ResponseEntity.ok().body(statisticsService.getSurveyPostList(surveyId));
    }



    //설문지 게시물 통계
    @GetMapping("/result/{postId}")
    public ResponseEntity<StatisticsResponse> getSurveyResultOfPost(@PathVariable Long postId){
        return ResponseEntity.ok().body(statisticsService.getPostResult(postId));
    }

    // 설문 게시물 참여자 목록
    @GetMapping("/result/userList/{surveyId}/{postId}")
    public ResponseEntity<?> getSurveyUserList(@PathVariable Long surveyId,
                                               @PathVariable Long postId){

        return ResponseEntity.ok().body(statisticsService.getSurveyUserList(surveyId, postId));
    }

    // 개인 설문 결과
    @GetMapping("/result/{surveyId}/{postId}/{nickname}")
    public ResponseEntity<?> getSurveyUserResult(@PathVariable Long surveyId,
                                                 @PathVariable Long postId,
                                                 @PathVariable String nickname){
        return ResponseEntity.ok().body(statisticsService.getSurveyUserResult(surveyId, postId, nickname));
    }

    // 점수형 설문 개별 통계
    @GetMapping("/result/score/{surveyId}/{postId}/{nickname}")
    public ResponseEntity<?> getScoreUserAnswer(@PathVariable Long surveyId,
                                                @PathVariable Long postId,
                                                @PathVariable String nickname){
        return ResponseEntity.ok().body(statisticsService.getScoreUserAnswer(surveyId, postId, nickname));

    }

    // 점수형 설문 정답
    @GetMapping("/result/score/{surveyId}")
    public ResponseEntity<?> getScoreAnswer(@PathVariable Long surveyId){
        return ResponseEntity.ok().body(statisticsService.getScoreAnswer(surveyId));
    }

    // 점수 설문 참여 사용자 목록
    @GetMapping("/result/score/userList/{surveyId}/{postId}")
    public ResponseEntity<?> getScoreUserList(@PathVariable Long surveyId,
                                              @PathVariable Long postId){
        return ResponseEntity.ok().body(statisticsService.getSurveyUserList(surveyId, postId));
    }

    // 점수 설문 전체 통계
    @GetMapping("/result/score/{surveyId}/{postId}")
    public ResponseEntity<?> getScoreResultOfPost(@PathVariable Long surveyId,
                                                  @PathVariable Long postId){
        return ResponseEntity.ok().body(statisticsService.getScoreResult(surveyId, postId));
    }




}
