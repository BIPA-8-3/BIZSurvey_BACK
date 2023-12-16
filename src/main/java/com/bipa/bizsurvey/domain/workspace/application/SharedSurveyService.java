package com.bipa.bizsurvey.domain.workspace.application;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.survey.domain.*;
import com.bipa.bizsurvey.domain.survey.dto.response.*;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.survey.enums.Correct;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.workspace.domain.*;
import com.bipa.bizsurvey.domain.workspace.dto.ContactDto;
import com.bipa.bizsurvey.domain.workspace.dto.SharedListDto;
import com.bipa.bizsurvey.domain.workspace.dto.SharedSurveyDto;
import com.bipa.bizsurvey.domain.workspace.dto.SharedSurveyResponseDto;
import com.bipa.bizsurvey.domain.workspace.repository.ContactRepository;
import com.bipa.bizsurvey.domain.workspace.repository.SharedListRepository;
import com.bipa.bizsurvey.domain.workspace.repository.SharedSurveyRepository;
import com.bipa.bizsurvey.domain.workspace.repository.SharedSurveyResponseRepository;
import com.bipa.bizsurvey.global.common.email.EmailMessage;
import com.bipa.bizsurvey.global.common.email.MailUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.ObjectInputFilter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class SharedSurveyService {
    private final SharedSurveyRepository sharedSurveyRepository;
    private final SharedSurveyResponseRepository sharedSurveyResponseRepository;
    private final SharedListRepository sharedListRepository;
    private final SurveyRepository surveyRepository;
    private final ContactRepository contactRepository;
    private final QuestionRepository questionRepository;

    private final MailUtil mailUtil;


    @Value("${domain.backend}")
    private String backendAddress;

    @Value("${domain.frontend}")
    private String frontendAddress;

    // 연락처로 공유
    public void share(SharedSurveyDto.SharedRequest request) {
        Long surveyId = request.getSurveyId();
        Survey survey = getSurvey(surveyId);
        Workspace workspace = survey.getWorkspace();

        // 공유 설문 Insert
        SharedSurvey sharedSurvey = SharedSurvey.builder()
                .survey(survey)
                .deadline(request.getDeadline())
                .build();

        sharedSurveyRepository.save(sharedSurvey);

        // 공유 데이터 저장
        List<ContactDto.SharedRequest> contactList = request.getContactList();

        List<SharedList> sharedList =
                contactList.stream().map(e -> {
                    Contact contact = getContact(e.getId());
                    return SharedList.builder()
                            .sharedSurvey(sharedSurvey)
                            .contact(contact)
                            .build();
                }).collect(Collectors.toList());


        sharedListRepository.saveAll(sharedList);

        // 메일 데이터 셍성
        Long sharedSurveyId = sharedSurvey.getId();
        String title = String.format("[BIZSURVEY] [%s] 워크스페이스에서 [%s] 설문을 요청하셨습니다.",
                workspace.getWorkspaceName(), survey.getTitle());


        List<EmailMessage> emailList = sharedList.stream().map(e -> {
            Contact contact = e.getContact();
            String email = contact.getEmail();
            Long id = e.getId();
            String token = null;

            try {
                token = mailUtil.encrypt(String.valueOf(id));
            } catch (Exception ex) {
                throw new RuntimeException("메일 전송에 실패하였습니다.");
            }

            EmailMessage emailMessage = EmailMessage.builder()
                    .to(email)
                    .subject(title)
                    .build();

            emailMessage.put("msg", "참여를 원하신다면 링크를 클릭해주세요. (링크는 3일간 유효합니다.)");
            emailMessage.put("hasLink", true);
            emailMessage.put("link", frontendAddress + "/authorization/shared/" + sharedSurveyId + "_" + token);
            emailMessage.put("linkText", "참여하기");

            return emailMessage;
        }).collect(Collectors.toList());

        // 메일 전송
        try {
            mailUtil.sendTemplateGroupMail(emailList);
        } catch (Exception e) {
            throw new RuntimeException("메일 전송에 실패하였습니다.");
        }
    }

    // 설문 참여
    public void participateSurvey(SharedSurveyDto.SharedSurveyAnswerRequest request) throws Exception {
        // 요청에서 필요한 정보 추출
        Long surveyId = request.getSurveyId();

        Long sharedListId = Long.parseLong(mailUtil.decrypt(request.getToken()));
        request.sortSharedAnswerListByQuestionId();

        // 공유 목록 및 답변 리스트 초기화
        SharedList sharedList = getSharedList(sharedListId);
        List<SharedSurveyDto.SharedAnswer> saveRequestList = request.getSharedAnswerList();

        // 설문 질문 목록 조회
        List<Question> questionList = questionRepository
                .findByIdInAndDelFlagFalseOrderById(saveRequestList.stream()
                        .map(SharedSurveyDto.SharedAnswer::getQuestionId)
                        .collect(Collectors.toList()));

        // 유효성 체크를 위한 필수 질문 목록 조회
        List<Long> requiredList = questionRepository
                .findIdByDelFlagFalseAndSurveyIdAndIsRequiredTrue(surveyId);

        // 유효성 체크
        validateSurveyData(saveRequestList, questionList, requiredList);

        // 답변 저장을 위한 리스트 초기화
        List<SharedSurveyResponse> saveList = new ArrayList<>();

        // 각 답변에 대한 처리
        IntStream.range(0, saveRequestList.size())
                .forEach(i -> processAnswer(saveRequestList.get(i), questionList.get(i), sharedList, saveList));

        // 답변 저장
        sharedSurveyResponseRepository.saveAll(saveList);
    }

    // 설문 데이터 유효성 체크
    private void validateSurveyData(List<SharedSurveyDto.SharedAnswer> answerList, List<Question> questionList, List<Long> requiredList) {
        if (answerList.size() != questionList.size()) {
            throw new RuntimeException("데이터가 변경되었습니다. 다시 시도해 주세요");
        }

        boolean containsAll = questionList.stream()
                .map(Question::getId)
                .collect(Collectors.toList())
                .containsAll(requiredList);

        if (!containsAll) {
            throw new RuntimeException("필수 질문을 포함하지 않습니다. 다시 시도해 주세요");
        }
    }

    // 답변 처리
    private void processAnswer(SharedSurveyDto.SharedAnswer answer, Question question,
                               SharedList sharedList, List<SharedSurveyResponse> saveList) {
        Boolean isRequired = question.getIsRequired();
        List<String> responseList = answer.getSurveyAnswer();

        // 필수 응답인 경우 응답이 비어 있을 경우 예외 처리
        if (Boolean.TRUE.equals(isRequired) && (responseList == null || responseList.size() == 0)) {
            throw new SurveyException(SurveyExceptionType.MISSING_REQUIRED_VALUE);
        }

        // 답변 저장
        for (String response : responseList) {
            saveList.add(SharedSurveyResponse.builder()
                    .surveyAnswer(response)
                    .answerType(answer.getAnswerType())
                    .sharedList(sharedList)
                    .question(question)
                    .url(answer.getUrl())
                    .build());
        }
    }

    // 링크 유효성 검사
    public Long linkValidation(Long sharedSurveyId, String token) {
        SharedSurvey sharedSurvey = sharedSurveyRepository.findByIdAndDelFlagFalse(sharedSurveyId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 링크입니다."));
        LocalDateTime deadline = sharedSurvey.getRegDate().plusDays(sharedSurvey.getDeadline());

        // deadline이 지났는지 확인
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("만료된 링크입니다.");
        }

        Long sharedListId = null;

        try {
            sharedListId = Long.parseLong(mailUtil.decrypt(token));
        } catch (Exception e) {
            throw new RuntimeException("유요하지 않는 링크입니다.");
        }

        boolean exists = sharedSurveyResponseRepository.existsByDelFlagFalseAndSharedListId(sharedListId);

        if (exists) {
            throw new RuntimeException("이미 참여한 설문입니다.");
        }
        return sharedSurvey.getSurvey().getId();
    }

    // 유효기간 연장
    public void deadlineExtension(Long shardSurveyId) {
        getShredSurvey(shardSurveyId).plusDeadline();
    }


    // 삭제
    public void delete(Long sharedListId) {
        SharedList sharedList = sharedListRepository.findById(sharedListId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 데이터입니다."));
        sharedList.delete();
    }

    // 설문지 조회
    private Survey getSurvey(Long surveyId) {
        return surveyRepository.findByIdAndDelFlagFalse(surveyId).orElseThrow(() ->
                new EntityNotFoundException("잘못된 설문지입니다."));
    }

    // 설문참여 목록 조회
    private SharedList getSharedList(Long sharedListId) {
        return sharedListRepository.findByIdAndDelFlagFalse(sharedListId).orElseThrow(() ->
                new EntityNotFoundException("잘못된 설문지입니다."));
    }

    // 공유 설문 조회
    private SharedSurvey getShredSurvey(Long surveyId) {
        return sharedSurveyRepository.findByIdAndDelFlagFalse(surveyId).orElseThrow(() ->
                new EntityNotFoundException("잘못된 설문지입니다."));
    }

    // 연락처 조회
    private Contact getContact(Long contactId) {
        return contactRepository.findByIdAndDelFlagFalse(contactId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 연락처 입니다."));
    }

    /////////////////////////////
    // 집계 로직 🥲전체적으로 수정 필요 //
    /////////////////////////////
    private final JPAQueryFactory jpaQueryFactory;

    QQuestion question = QQuestion.question;
    QAnswer answer = QAnswer.answer;
    QSharedSurveyResponse ssr = QSharedSurveyResponse.sharedSurveyResponse;
    QSharedSurvey ss = QSharedSurvey.sharedSurvey;
    QContact ct = QContact.contact;
    QSharedList sl = QSharedList.sharedList;

    // 공유단위 목록 조회
    public List<SharedSurveyDto.SharedSurveysResponse> readSharedSurveyHistory(Long surveyId) {
        List<SharedSurvey> sharedSurveys = sharedSurveyRepository.findBySurveyIdAndDelFlagFalseOrderByModDateDesc(surveyId);
        return sharedSurveys.stream().map(e -> {
            LocalDateTime dueDate = e.getRegDate().plusDays(e.getDeadline());
            return SharedSurveyDto.SharedSurveysResponse.builder()
                    .id(e.getId())
                    .regDate(e.getRegDate())
                    .dueDate(dueDate)
                    .deadline(LocalDateTime.now().isAfter(dueDate)) // true 마감일자 안 지남
                    .surveyId(e.getSurvey().getId())
                    .build();
        }).collect(Collectors.toList());
    }

    // 공유 단위별 참여자 목록
    public List<SharedListDto.Response> readSharedContactList(Long sharedSurveyId) {
//        return jpaQueryFactory.select(Projections.fields(SharedListDto.Response.class,
//                        sl.id,
//                        sl.sharedSurvey.id.as("sharedSurveyId"),
//                        ct.id.as("contactId"),
//                        ct.email,
//                        ct.name
//                ))
//                .from(sl)
//                .leftJoin(ct).on(sl.contact.eq(ct))
//                .where(sl.sharedSurvey.id.eq(sharedSurveyId).and(sl.delFlag.isFalse())
//                        .and(JPAExpressions
//                                .select(ssr.count())
//                                .from(ssr)
//                                .where(ssr.sharedList.eq(sl)).gt(0L)ScoreResultResponse))
//                .fetch();


        return jpaQueryFactory.select(Projections.fields(SharedListDto.Response.class,
                        sl.id,
                        sl.sharedSurvey.id.as("sharedSurveyId"),
                        ct.id.as("contactId"),
                        ct.email,
                        ct.name,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(ssr.count())
                                        .from(ssr)
                                        .where(ssr.sharedList.eq(sl)),
                                "response")
                ))
                .from(sl)
                .leftJoin(ct).on(sl.contact.eq(ct))
                .where(sl.sharedSurvey.id.eq(sharedSurveyId).and(sl.delFlag.isFalse()))
                .fetch();
    }

    // 개인 결과
    public List<SharedSurveyResponseDto.QuestionResponse> readSharedSurveyListResult(Long sharedListId) {
        return jpaQueryFactory.select(Projections.constructor(SharedSurveyResponseDto.QuestionResponse.class,
                        ssr.question.id,
                        ssr.answerType,
                        ssr.surveyAnswer,
                        ssr.url))
                .from(ssr)
                .where(ssr.delFlag.eq(false).and(ssr.sharedList.id.eq(sharedListId)))
                .fetch();
    }


    // 외부공유 통계
    public StatisticsResponse readSharedSurveyResult(Long sharedSurveyId) {
        return new StatisticsResponse(processChartAndText(sharedSurveyId), processFile(sharedSurveyId));
    }


    // 개별 점수형 설문 정답
    public List<SharedSurveyResponseDto.PersonalScoreSurveyResults> readPersonalScoreResults(Long sharedListId) {
        return jpaQueryFactory.select(
                        Projections.constructor(SharedSurveyResponseDto.PersonalScoreSurveyResults.class,
                                ssr.question.id,
                                Projections.list(ssr.surveyAnswer)
                        ))
                .from(ssr)
                .where(ssr.delFlag.eq(false)
                        .and(ssr.sharedList.id.eq(sharedListId)))
                .fetch();
    }

    // 공유 단위 점수 통계
    public List<ScoreResultResponse> readShareScoreResults(Long surveyId, Long sharedSurveyId) {
        List<Long> sharedListIds = jpaQueryFactory.select(sl.id)
                .from(sl)
                .where(sl.sharedSurvey.id.eq(sharedSurveyId)).fetch();

        List<Question> questionList = jpaQueryFactory
                .select(question)
                .from(question)
                .where(question.delFlag.isFalse().and(question.survey.id.eq(surveyId))).fetch();

        List<ScoreResultResponse> result = jpaQueryFactory.select(
                        Projections.constructor(ScoreResultResponse.class,
                                answer.question.id,
                                answer.question.surveyQuestion,
                                Projections.list(
                                        Projections.constructor(
                                                ScoreAnswerCount.class,
                                                answer.surveyAnswer,
                                                JPAExpressions.select(ssr.count())
                                                        .from(ssr)
                                                        .where(ssr.question.eq(answer.question)
                                                                .and(ssr.surveyAnswer.eq(answer.surveyAnswer))
                                                                .and(ssr.sharedList.id.in(sharedListIds))),
                                                answer.correct
                                        )
                                )
                        )
                )
                .from(answer)
                .where(answer.delFlag.isFalse().and(answer.question.in(questionList)))
                .fetch();

        Map<Long, List<ScoreResultResponse>> groupResults = result
                        .stream()
                        .collect(Collectors.groupingByConcurrent(ScoreResultResponse::getQuestionId));

        return groupResults.entrySet().stream().map(entry -> {
            Long questionId = entry.getKey();
            List<ScoreResultResponse> scoreList = entry.getValue();
            String title = scoreList.get(0).getTitle();

            List<ScoreAnswerCount> mergedAnswers = scoreList.stream()
                    .flatMap(score -> score.getAnswers().stream()).collect(Collectors.toList());

            return new ScoreResultResponse(questionId, title, mergedAnswers);
        }).collect(Collectors.toList());
    }

    private BooleanBuilder createStatisticalConditions(Long sharedSurveyId, AnswerType answerType) {
        BooleanBuilder whereClause = new BooleanBuilder()
                .and(ssr.delFlag.isFalse())
                .and(ssr.question.delFlag.isFalse());

        if (answerType != AnswerType.FILE) {
            whereClause.and(ssr.question.answerType.ne(AnswerType.FILE))
                    .and(ssr.answerType.ne(AnswerType.FILE));
        } else {
            whereClause.and(ssr.question.answerType.eq(AnswerType.FILE))
                    .and(ssr.answerType.eq(AnswerType.FILE));
        }

        whereClause.and(ssr.sharedList.id.in(
                JPAExpressions.select(sl.id)
                        .from(sl)
                        .where(sl.delFlag.isFalse()
                                .and(sl.sharedSurvey.id.eq(sharedSurveyId)))));

        return whereClause;
    }

    public List<ChartAndTextResponse> processChartAndText(Long sharedSurveyId) {
        List<ChartAndTextResponse> chartResult = jpaQueryFactory.select(Projections.constructor(ChartAndTextResponse.class,
                        ssr.question.id,
                        ssr.question.surveyQuestion,
                        ssr.question.answerType.as("questionType"),
                        Projections.list(Projections.constructor(ChartAndTextResult.class, ssr.surveyAnswer.as("answer"),
                                ssr.count()))
                ))
                .from(ssr)
                .where(createStatisticalConditions(sharedSurveyId, null))
                .groupBy(ssr.question, ssr.surveyAnswer)
                .orderBy(ssr.question.step.asc())
                .fetch();

        chartResult.stream()
                .collect(Collectors.groupingByConcurrent(ChartAndTextResponse::getQuestionId))
                .values();

        return chartResult.stream()
                .collect(Collectors.groupingByConcurrent(ChartAndTextResponse::getQuestionId))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long questionId = entry.getKey();
                    List<ChartAndTextResponse> chartResultResponse = entry.getValue();

                    AnswerType answerType = chartResultResponse.get(0).getQuestionType();
                    List<ChartAndTextResult> answers = chartResultResponse.stream().flatMap(chartResponse -> chartResponse.getAnswers().stream())
                            .collect(Collectors.toList());

                    return new ChartAndTextResponse(questionId, null, answerType, answers);
                }).collect(Collectors.toList());
    }

    private List<FileResultResponse> processFile(Long sharedSurveyId) {
        List<FileResultResponse> fileResult = jpaQueryFactory.select(Projections.constructor(FileResultResponse.class,
                        ssr.question.id,
                        ssr.question.surveyQuestion,
                        ssr.question.answerType.as("questionType"),
                        Projections.list(Projections.constructor(FileInfo.class, ssr.surveyAnswer.as("fileName"),
                                ssr.url))
                ))
                .from(ssr)
                .where(createStatisticalConditions(sharedSurveyId, AnswerType.FILE))
                .fetch();

        return fileResult.stream().collect(Collectors.groupingByConcurrent(FileResultResponse::getQuestionId))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long questionId = entry.getKey();
                    List<FileResultResponse> fileResultResponses = entry.getValue();
                    List<FileInfo> resultFileInfo = fileResultResponses.stream().flatMap(fileResultResponse -> fileResultResponse.getFileInfos().stream()).collect(Collectors.toList());
                    return new FileResultResponse(questionId, null, AnswerType.FILE, resultFileInfo);
                }).collect(Collectors.toList());
    }
}