package com.bipa.bizsurvey.domain.workspace.api;


import com.bipa.bizsurvey.domain.survey.dto.response.StatisticsResponse;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.workspace.application.SharedSurveyService;
import com.bipa.bizsurvey.domain.workspace.dto.SharedListDto;
import com.bipa.bizsurvey.domain.workspace.dto.SharedSurveyDto;
import com.bipa.bizsurvey.domain.workspace.dto.SharedSurveyResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace/shared-survey")
@Log4j2
public class SharedSurveyController {

    private final SharedSurveyService sharedSurveyService;

    @PostMapping
    public ResponseEntity<String> share(@AuthenticationPrincipal LoginUser loginUser,
                                        @RequestBody SharedSurveyDto.SharedRequest request) {

        sharedSurveyService.share(request);
        return ResponseEntity.ok().body("성공적으로 전송하였습니다.");
    }

    @GetMapping("/link/{sharedSurveyId}/{token}")
    public ResponseEntity<Long> getSurveyInfo(@PathVariable Long sharedSurveyId,
                                              @PathVariable String token) {
        return ResponseEntity.ok().body(sharedSurveyService.linkValidation(sharedSurveyId, token));
    }

    @PostMapping("/survey")
    public ResponseEntity<String> participateSurvey(@RequestBody SharedSurveyDto.SharedSurveyAnswerRequest response) {
        try {
            sharedSurveyService.participateSurvey(response);
            return ResponseEntity.ok().body("설문 참여를 완료하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("설문 참여에 실패하셨습니다.");
        }
    }

    @PutMapping("/{sharedSurveyId}")
    public ResponseEntity<String> deadlineExtension(@PathVariable Long sharedSurveyId) {
        sharedSurveyService.deadlineExtension(sharedSurveyId);
        return ResponseEntity.ok().body("마감기한이 연장되었습니다.");
    }

    @DeleteMapping("/{sharedListId}")
    public ResponseEntity<String> remove(@PathVariable Long sharedListId) {
        sharedSurveyService.delete(sharedListId);
        return ResponseEntity.ok().body("삭제가 완료되었습니다.");
    }

    // 공유 내역
    @GetMapping("/{surveyId}")
    public ResponseEntity<List<SharedSurveyDto.SharedSurveysResponse>> readSharedSurveyHistory(@PathVariable Long surveyId) {
        return ResponseEntity.ok().body(sharedSurveyService.readSharedSurveyHistory(surveyId));
    }

    @GetMapping("/survey/{sharedSurveyId}")
    public ResponseEntity<List<SharedListDto.Response>> readSharedContactList(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long sharedSurveyId) {
        return ResponseEntity.ok().body(sharedSurveyService.readSharedContactList(sharedSurveyId));
    }

    // 집계
    @GetMapping("/personal/result/{sharedListId}")
    public ResponseEntity<List<SharedSurveyResponseDto.QuestionResponse>> readSharedSurveyListResult(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long sharedListId) {
        return ResponseEntity.ok().body(sharedSurveyService.readSharedSurveyListResult(sharedListId));
    }

    // 외부공유 통계
    @GetMapping("/external/{sharedSurveyId}")
    public ResponseEntity<StatisticsResponse> readSharedSurveyResult(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long sharedSurveyId) {
        return ResponseEntity.ok().body(sharedSurveyService.readSharedSurveyResult(sharedSurveyId));
    }

    // 점수형 통계
    @GetMapping("/personal/score/result/{sharedSurveyId}")
    public ResponseEntity<List<SharedSurveyResponseDto.PersonalScoreSurveyResults>> readPersonalScoreResults(@PathVariable Long sharedSurveyId) {
        return ResponseEntity.ok().body(sharedSurveyService.readPersonalScoreResults(sharedSurveyId));
    }

    @GetMapping("/score/result/{surveyId}/{sharedSurveyId}")
    public ResponseEntity<?> readShareScoreResults(
            @PathVariable Long surveyId,
            @PathVariable Long sharedSurveyId) {
        return ResponseEntity.ok().body(sharedSurveyService.readShareScoreResults(surveyId, sharedSurveyId));
    }

    // 공유 토큰 확인
//    @GetMapping("/invite/{token}")
//    public ResponseEntity<String> checkInvitationCode(@PathVariable String token) {
//        if(!redisService.validateDataExists("INVITE-" + token)) {
//            return ResponseEntity.ok().body(token);
//        }else {
//            return ResponseEntity.badRequest().body("유효하지 않은 링크입니다.");
//        }
//    }
}
