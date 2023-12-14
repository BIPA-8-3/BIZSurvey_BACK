package com.bipa.bizsurvey.domain.workspace.application;

import com.bipa.bizsurvey.domain.survey.domain.QAnswer;
import com.bipa.bizsurvey.domain.survey.domain.QQuestion;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
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
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
            emailMessage.put("linkText", "입장하기");

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
    public void participateSurvey(SharedSurveyDto.SharedSurveyAnswerResponse response) {
        Long sharedSurveyId = response.getSharedSurveyId();
        Long sharedListId = response.getSharedListId();

        response.sortSharedAnswerListByQuestionId();
        List<SharedSurveyDto.SharedAnswer> saveRequestList = response.getSharedAnswerList();

        SharedSurvey sharedSurvey = getShredSurvey(sharedListId);
        SharedList sharedList = getSurveyList(sharedListId);

        Long surveyId = sharedSurvey.getSurvey().getId();

        List<Question> questionList = questionRepository.findAllBySurveyIdAndDelFlagFalseOrderByStep(surveyId);
        List<SharedSurveyResponse> answerList = new ArrayList<>();


        for (int i = 0; i < saveRequestList.size(); i++) {
            Question question = questionList.get(i);
            SharedSurveyDto.SharedAnswer answer = saveRequestList.get(i);
            Boolean b = question.getIsRequired();

            if (b != null && b && (answer.getSurveyAnswer() == null || answer.getSurveyAnswer().isEmpty())) {
                throw new SurveyException(SurveyExceptionType.MISSING_REQUIRED_VALUE);
            }

            answerList.add(SharedSurveyResponse.builder()
                    .surveyAnswer(answer.getSurveyAnswer())
                    .sharedSurvey(sharedSurvey)
                    .sharedList(sharedList)
                    .question(question)
                    .url(answer.getUrl())
                    .fileName(answer.getFilaName())
                    .build());
        }

        sharedSurveyResponseRepository.saveAll(answerList);
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
            throw new RuntimeException("만료된 링크입니다.");
        }

        boolean exists = sharedSurveyResponseRepository.existsByIdAndDelFlagFalseAndSharedSurveyIdAndSharedListId(sharedListId, sharedSurvey.getId(), sharedListId);

        if (exists) {
            throw new RuntimeException("이미 참가한 설문입니다.");
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
    private SharedList getSurveyList(Long surveyListId) {
        return sharedListRepository.findByIdAndDelFlagFalse(surveyListId).orElseThrow(() ->
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
    public List<SharedSurveyResponseDto.QuestionResponse> readSharedSurveyListResult(Long surveyId, Long sharedSurveyId, Long sharedListId) {
        return jpaQueryFactory
                .select(Projections.constructor(SharedSurveyResponseDto.QuestionResponse.class,
                        question.id,
                        question.surveyQuestion,
                        question.answerType,
                        ssr.surveyAnswer,
                        ssr.fileName,
                        ssr.url
                ))
                .from(question)
                .leftJoin(ssr)
                .on(ssr.sharedList.id.eq(sharedListId)
                        .and(ssr.sharedSurvey.id.eq(sharedSurveyId))
                        .and(question.eq(ssr.question)))
                .where(question.delFlag.eq(false)
                        .and(ssr.delFlag.eq(false))
                        .and(question.survey.id.eq(surveyId)))
                .fetch();
    }

    // 외부공유 통계
    public List<SharedSurveyResponseDto.QuestionTotalResponse> readSharedSurveyResult(Long surveyId, Long sharedSurveyId) {
        List<SharedSurveyResponseDto.QuestionResultResponse> result1 =
                jpaQueryFactory.select(
                                Projections.constructor(SharedSurveyResponseDto.QuestionResultResponse.class,
                                        question.id.as("questionId"),
                                        Projections.list(Projections.constructor(String.class, ssr.surveyAnswer.as("answer")))))
                        .from(question)
                        .leftJoin(ssr)
                        .on(question.eq(ssr.question))
                        .where(question.delFlag.isFalse()
                                .and(ssr.delFlag.isFalse())
                                .and(question.survey.id.eq(surveyId))
                                .and(ssr.sharedSurvey.id.eq(sharedSurveyId))
                                .and(question.answerType.notIn(AnswerType.valueOf("FILE"), AnswerType.valueOf("SINGLE_CHOICE"), AnswerType.valueOf("MULTIPLE_CHOICE"))))
                        .fetch();

        List<SharedSurveyResponseDto.ChartResultResponse> result2 =
                jpaQueryFactory.select(
                                Projections.constructor(SharedSurveyResponseDto.ChartResultResponse.class,
                                        question.id,
                                        Projections.list(
                                                Projections.constructor(
                                                        SharedSurveyResponseDto.ChartInfo.class,
                                                        ssr.surveyAnswer.as("answer"),
                                                        ssr.surveyAnswer.count().as("count")))
                                )
                        ).from(question)
                        .leftJoin(ssr)
                        .on(question.eq(ssr.question))
                        .where(question.delFlag.isFalse()
                                .and(ssr.delFlag.isFalse())
                                .and(question.survey.id.eq(surveyId))
                                .and(ssr.sharedSurvey.id.eq(sharedSurveyId))
                                .and(question.answerType.in(AnswerType.valueOf("SINGLE_CHOICE"), AnswerType.valueOf("MULTIPLE_CHOICE"))))
                        .groupBy(question.id, ssr.surveyAnswer)
                        .fetch();

        List<SharedSurveyResponseDto.FileResultResponse> result3 =
                jpaQueryFactory.select(
                                Projections.constructor(SharedSurveyResponseDto.FileResultResponse.class,
                                        question.id,
                                        Projections.list(Projections.constructor(SharedSurveyResponseDto.FileInfo.class,
                                                ssr.fileName,
                                                ssr.url))
                                )
                        ).from(question)
                        .leftJoin(ssr)
                        .on(question.eq(ssr.question))
                        .where(question.delFlag.isFalse()
                                .and(ssr.delFlag.isFalse())
                                .and(question.survey.id.eq(surveyId))
                                .and(ssr.sharedSurvey.id.eq(sharedSurveyId))
                                .and(question.answerType.eq(AnswerType.valueOf("FILE"))))
                        .fetch();


        List<Question> questionList = questionRepository.findAllBySurveyIdAndDelFlagFalseOrderByStep(surveyId);

        List<SharedSurveyResponseDto.QuestionTotalResponse> result =
                questionList.stream().map(e -> {
                    AnswerType type = e.getAnswerType();

                    SharedSurveyResponseDto.QuestionTotalResponse response =
                            SharedSurveyResponseDto.QuestionTotalResponse.builder()
                                    .questionId(e.getId())
                                    .question(e.getSurveyQuestion())
                                    .questionType(e.getAnswerType())
                                    .build();

                    switch (type) {
                        case SINGLE_CHOICE:
                        case MULTIPLE_CHOICE:
                            SharedSurveyResponseDto.ChartResultResponse temp2 = result2.stream().filter(r -> r.getQuestionId().equals(response.getQuestionId())).findFirst().get();
                            response.setChartInfo(temp2.getChartInfo());
                            break;
                        case FILE:
                            SharedSurveyResponseDto.FileResultResponse temp3 = result3.stream().filter(r -> r.getQuestionId().equals(response.getQuestionId())).findFirst().get();
                            response.setFileInfo(temp3.getFileInfos());
                            break;
                        default:
                            SharedSurveyResponseDto.QuestionResultResponse temp1 = result1.stream().filter(r -> r.getQuestionId().equals(response.getQuestionId())).findFirst().get();
                            response.setAnswerList(temp1.getAnswer());
                            break;
                    }

                    return response;
                }).collect(Collectors.toList());

        return result;
    }

    // 개별 점수형 설문 정답
    public List<SharedSurveyResponseDto.PersonalScoreSurveyResults> readPersonalScoreResults(Long surveyId, Long sharedSurveyId, Long sharedListId) {
        QQuestion question = QQuestion.question;
        QAnswer answer = QAnswer.answer;
        QSharedSurveyResponse ssr = QSharedSurveyResponse.sharedSurveyResponse;

        return jpaQueryFactory.select(Projections.constructor(
                        SharedSurveyResponseDto.PersonalScoreSurveyResults.class,
                        question.id,
                        question.surveyQuestion,
                        question.score,
                        Projections.list(Projections.constructor(String.class, answer.surveyAnswer)),
                        Projections.list(Projections.constructor(String.class, ssr.surveyAnswer)),
                        new CaseBuilder()
                                .when(Expressions.booleanTemplate("?1 = ?2",
                                        Expressions.list(Projections.constructor(String.class, answer.surveyAnswer)),
                                        Expressions.list(Projections.constructor(String.class, ssr.surveyAnswer))))
                                .then(question.score)
                                .otherwise(0).as("myScore")
                ))
                .from(question)
                .leftJoin(answer)
                .on(question.eq(answer.question))
                .leftJoin(ssr)
                .on(ssr.sharedList.id.eq(sharedListId).and(question.eq(ssr.question)))
                .where(question.survey.id.eq(surveyId)
                        .and(ssr.sharedSurvey.id.eq(sharedSurveyId))
                        .and(answer.delFlag.isFalse())
                        .and(question.delFlag.isFalse()))
                .fetch();
    }


    // 공유 단위 점수 통계
    public List<SharedSurveyResponseDto.ShareScoreResults> readShareScoreResults(Long surveyId, Long sharedSurveyId) {


        List<SharedSurveyResponseDto.ShareScoreResults> result =
                jpaQueryFactory.select(
                                Projections.constructor(SharedSurveyResponseDto.ShareScoreResults.class,
                                        question.id,
                                        question.surveyQuestion,
                                        Projections.list(Projections.constructor(String.class, answer.surveyAnswer)),
                                        question.score,
                                        Projections.list(Projections.constructor(SharedSurveyResponseDto.ShareScoreAnswer.class,
                                                ssr.sharedList.id,
                                                Projections.list(Projections.constructor(String.class, ssr.surveyAnswer))
                                        ))
                                )
                        )
                        .from(question)
                        .leftJoin(answer)
                        .on(answer.delFlag.isFalse().and(question.eq(answer.question)))
                        .leftJoin(ssr)
                        .on(ssr.delFlag.isFalse().and(ssr.sharedSurvey.id.eq(sharedSurveyId)).and(ssr.question.eq(question)))
                        .where(question.delFlag.isFalse()
                                .and(question.survey.id.eq(surveyId)))
                        .fetch();


        for (int i = 0; i < result.size(); i++) {
            SharedSurveyResponseDto.ShareScoreResults temp = result.get(i);
            List<String> correctAnswer = temp.getCorrectAnswer();
            List<SharedSurveyResponseDto.ShareScoreAnswer> sharedAnswer = temp.getAnswer();
            AtomicInteger cnt = new AtomicInteger();
            sharedAnswer.stream().filter(e -> e.getQuestion().stream().allMatch(correctAnswer::contains)).forEach(e -> {
                cnt.getAndIncrement();
                e.setScore(temp.getScore());
            });
            temp.setCorrectCnt(cnt.get());
        }

        return result;
    }
}