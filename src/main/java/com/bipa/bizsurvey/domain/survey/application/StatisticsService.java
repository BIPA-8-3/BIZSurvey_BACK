package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.QSurveyPost;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.QAnswer;
import com.bipa.bizsurvey.domain.survey.domain.QUserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.response.*;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.survey.enums.Correct;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.survey.repository.UserSurveyResponseRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.application.SharedSurveyService;
import com.bipa.bizsurvey.global.common.storage.FileUtil;
import com.bipa.bizsurvey.global.common.storage.ShareType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {


    //

    private final SurveyPostRepository surveyPostRepository;
    private final QuestionRepository questionRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SharedSurveyService sharedSurveyService;
    public QUserSurveyResponse u = new QUserSurveyResponse("u");
    public QSurveyPost sp = new QSurveyPost("sp");
    public QAnswer a = new QAnswer("a");

    //설문지 등록된 게시물 목록
    public List<SurveyPostListResponse> getSurveyPostList(Long surveyId){
        return jpaQueryFactory
                .select(Projections.constructor(SurveyPostListResponse.class, sp.post.id, sp.post.title))
                .from(sp)
                .innerJoin(sp.post)
                .where(sp.survey.id.eq(surveyId))
                .fetch();
    }

    //설문지 게시물 참여자 목록

    public List<ParticipantList> getSurveyUserList(Long surveyId, Long postId) {
        SurveyPost surveyPost = surveyPostRepository.findByPostIdAndSurveyId(postId, surveyId);

        return jpaQueryFactory
                .select(Projections.constructor(ParticipantList.class, u.user.id, u.user.nickname))
                .from(u)
                .innerJoin(u.user)
                .where(u.surveyPost.eq(surveyPost))
                .groupBy(u.user.id)
                .fetch();
    }

    // 개인 결과

    public List<PersonalResultResponse> getSurveyUserResult(Long surveyId, Long postId, Long userId) {

        SurveyPost surveyPost = surveyPostRepository.findByPostIdAndSurveyId(postId, surveyId);
        User user = userRepository.findById(userId).orElseThrow();

        return jpaQueryFactory
                .select(Projections.constructor(PersonalResultResponse.class,
                        u.question.id,
                        u.answer,
                        u.url,
//                        u.question.answerType,
                        u.answerType
                        ))
                .from(u)
                .innerJoin(u.question)
                .where(u.user.id.eq(user.getId())
                        .and(u.surveyPost.id.eq(surveyPost.getId()))
                        .and(u.question.delFlag.eq(false)))
                .fetch();
    }

    //게시물 통계

    public StatisticsResponse getPostResult(Long postId) {
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyPost.getSurvey();

        List<ChartAndTextResponse> chartResults = processChartAndText(survey, surveyPost);
        List<FileResultResponse> fileResults = processFile(survey, surveyPost);

        return new StatisticsResponse(chartResults, fileResults);
    }



    // 점수형 설문 개별 통계

    public List<UserScoreResponse> getScoreUserAnswer(Long surveyId, Long postId, Long userId){
        SurveyPost surveyPost = surveyPostRepository.findByPostIdAndSurveyId(postId, surveyId);
        User user = userRepository.findById(userId).orElseThrow();

        List<UserScoreResponse> list = jpaQueryFactory
                .select(Projections.constructor(UserScoreResponse.class,
                        u.question.id,
                        u.question.score,
                        Projections.list(
                                u.answer
                        )))
                .from(u)
                .innerJoin(a).on(u.question.id.eq(a.question.id))
                .innerJoin(u.question)
                .where(u.surveyPost.id.eq(surveyPost.getId())
                        .and(u.user.id.eq(user.getId()))
                        .and(u.answer.eq(a.surveyAnswer))
                        .and(a.delFlag.eq(false)))
                .groupBy(u.question, a.correct, u.answer)
                .orderBy(u.question.step.asc())
                .fetch();

        Map<Long, UserScoreResponse> answerMap = new HashMap<>();

        for(UserScoreResponse answer : list){
            Long questionId = answer.getQuestionId();

            if(answerMap.containsKey(questionId)){
                List<String> existingAnswers = new ArrayList<>(answerMap.get(questionId).getUserAnswer());

                existingAnswers.addAll(answer.getUserAnswer());

                answerMap.get(questionId).setUserAnswer(existingAnswers);
            }else{
                answerMap.put(questionId, answer);
            }
        }


        return new ArrayList<>(answerMap.values());
    }

    // 점수형 설문 정답
    public List<ScoreAnswerResponse> getScoreAnswer(Long surveyId){

        return jpaQueryFactory
                .select(Projections.constructor(ScoreAnswerResponse.class,
                        a.question.id,
                        a.surveyAnswer))
                .from(a)
                .innerJoin(a.question)
                .where(a.question.delFlag.eq(false)
                        .and(a.question.survey.id.eq(surveyId))
                        .and(a.correct.eq(Correct.YES))
                        .and(a.delFlag.eq(false)))
                .fetch();
    }

    // 점수형 게시물 통계
    public List<ScoreResultResponse> getScoreResult(Long postId){

        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyPost.getSurvey();

        List<ScoreResultResponse> list = jpaQueryFactory
                .select(Projections.constructor(ScoreResultResponse.class,
                        a.question.id,
                        a.question.surveyQuestion,
                        a.question.step,
                        Projections.list(Projections.constructor(ScoreAnswerCount.class,
                                a.surveyAnswer,
                                u.answer.count(),
                                a.correct))
                        ))
                .from(a)
                .leftJoin(u).on(a.question.eq(u.question).and(u.surveyPost.eq(surveyPost)).and(u.answer.eq(a.surveyAnswer)))
                .where(a.delFlag.eq(false)
                        .and(a.question.survey.eq(survey)))
                .groupBy(a.surveyAnswer, a.question, a.correct)
                .orderBy(a.question.step.asc())
                .fetch();


        Map<Long, List<ScoreResultResponse>> groupedResults = list.stream()
                .collect(Collectors.groupingBy(ScoreResultResponse::getQuestionId));


        List<ScoreResultResponse> processedResults = groupedResults.entrySet().stream()
                .map(entry -> {
                    long questionId = entry.getKey();
                    List<ScoreResultResponse> scoreList = entry.getValue();
                    String title = scoreList.get(0).getTitle();
                    int step = scoreList.get(0).getStep();

                    List<ScoreAnswerCount> mergedAnswers = entry.getValue().stream()
                            .flatMap(result -> result.getAnswers().stream())
                            .collect(Collectors.toList());
                    return new ScoreResultResponse(questionId, title, step,mergedAnswers);
                })
                .collect(Collectors.toList());

        return processedResults;

    }



    //기본 설문 게시물 차트,텍스트 통계
    public List<ChartAndTextResponse> processChartAndText(Survey survey, SurveyPost surveyPost){

        List<ChartAndTextResponse> results = jpaQueryFactory
                .select(Projections.constructor(ChartAndTextResponse.class, u.question.id,u.question.surveyQuestion, u.question.answerType.as("questionType"),
                        Projections.list(Projections.constructor(ChartAndTextResult.class, u.answer, u.count().as("count"))))
                )
                .from(u)
                .innerJoin(u.question)
                .where(u.question.survey.eq(survey)
                        .and(u.question.delFlag.eq(false))
                        .and(u.surveyPost.eq(surveyPost))
                        .and(u.question.answerType.notIn(AnswerType.FILE))
                        .and(u.answerType.notIn(AnswerType.FILE)))
                .groupBy(u.question, u.answer)
                .orderBy(u.question.step.asc())
                .fetch();

        return results.stream()
                .collect(Collectors.groupingBy(ChartAndTextResponse::getQuestionId))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long questionId = entry.getKey();
                    List<ChartAndTextResponse> chartResultResponses = entry.getValue();

                    AnswerType answerType = chartResultResponses.get(0).getQuestionType();
                    String title = chartResultResponses.get(0).getTitle();

                    List<ChartAndTextResult> combinedAnswers = chartResultResponses.stream()
                            .flatMap(chartResultResponse -> chartResultResponse.getAnswers().stream())
                            .collect(Collectors.toList());
                    return new ChartAndTextResponse(questionId, title, answerType, combinedAnswers);
                })
                .collect(Collectors.toList());

    }




    public List<FileResultResponse> processFile(Survey survey, SurveyPost surveyPost){
        List<FileResultResponse> results = jpaQueryFactory
                .select(Projections.constructor(FileResultResponse.class,
                        u.question.id,
                        u.question.surveyQuestion, u.question.answerType.as("questionType"),
                        Projections.list(Projections.constructor(FileInfo.class, u.answer.as("filename"), u.url)))
                )
                .from(u)
                .innerJoin(u.question)
                .where(u.question.survey.eq(survey)
                        .and(u.question.delFlag.eq(false))
                        .and(u.surveyPost.eq(surveyPost))
                        .and(u.question.answerType.in(AnswerType.FILE))
                        .and(u.answerType.in(AnswerType.FILE)))
                .fetch();

        return results.stream()
                .collect(Collectors.groupingBy(FileResultResponse::getQuestionId))
                .entrySet()
                       .stream()
                .map(entry -> {
                    Long questionId = entry.getKey();
                    List<FileResultResponse> fileResultResponses = entry.getValue();

                    AnswerType answerType = fileResultResponses.get(0).getQuestionType();
                    String title = fileResultResponses.get(0).getTitle();

                    List<FileInfo> combinedAnswers = fileResultResponses.stream()
                            .flatMap(fileResultResponse -> fileResultResponse.getFileInfos().stream())
                            .collect(Collectors.toList());
                    return new FileResultResponse(questionId, title, answerType, combinedAnswers);
                })
                .collect(Collectors.toList());
    }


    public void downloadExcelResult(HttpServletResponse response, Long sharedId, ShareType shareType) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("통계결과");
        int rowNo = 0;

        Row headerRow = sheet.createRow(rowNo++);

        headerRow.createCell(0).setCellValue("질문");
        headerRow.createCell(1).setCellValue("유형");
        headerRow.createCell(2).setCellValue("답변");
        headerRow.createCell(3).setCellValue("응답자 수");

        List<ChartAndTextResponse> result = null;

        if(ShareType.INTERNAL.equals(shareType)) {
            SurveyPost surveyPost = surveyPostRepository.findByPostId(sharedId);
            Survey survey = surveyPost.getSurvey();
            result = processChartAndText(survey, surveyPost);

        } else {
            result = sharedSurveyService.processChartAndText(sharedId);
        }

        for(ChartAndTextResponse list : result){
            for (ChartAndTextResult answerCount : list.getAnswers()) {
                Row dataRow = sheet.createRow(rowNo++);
                dataRow.createCell(0).setCellValue(list.getTitle());
                dataRow.createCell(1).setCellValue(list.getQuestionType().getValue());
                dataRow.createCell(2).setCellValue(answerCount.getAnswer());
                dataRow.createCell(3).setCellValue(answerCount.getCount());
            }
        }

        String filename = "통계.xlsx";

        response.setContentType("application/octet-stream;");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(filename.getBytes("UTF-8"), "ISO-8859-1"));
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");

        // excel 파일 저장
        try {
            workbook.write(response.getOutputStream());
            response.getOutputStream().close();
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

