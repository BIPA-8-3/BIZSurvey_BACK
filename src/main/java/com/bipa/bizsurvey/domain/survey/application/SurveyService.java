package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.domain.*;
import com.bipa.bizsurvey.domain.survey.dto.request.*;
import com.bipa.bizsurvey.domain.survey.dto.response.AnswerResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.QuestionResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyListResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResponse;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType;
import com.bipa.bizsurvey.domain.survey.repository.AnswerRepository;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceAdminRepository;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SurveyService {


    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceAdminRepository workspaceAdminRepository;
    private final JPAQueryFactory jpaQueryFactory;
    public QSurvey s = new QSurvey("s");
    public QQuestion q = new QQuestion("q");
    public QAnswer a = new QAnswer("a");
    public QSurvey s1 = new QSurvey("s1");

    public List<SurveyListResponse> getSurveyList(Long workspaceId, String fieldName){
        return jpaQueryFactory
                .select(Projections.constructor(SurveyListResponse.class, s.id, s.title, s.surveyType))
                .from(s)
                .where(s.workspace.id.eq(workspaceId)
                        .and(s.delFlag.eq(false))
                        .and(Expressions.list(s.surveyGroup, s.version)
                                .in(JPAExpressions
                                        .select(s1.surveyGroup, s1.version.max())
                                        .from(s1)
                                        .groupBy(s1.surveyGroup)
                                )
                        )
                )
                .orderBy(sortByField(fieldName))
                .fetch();
    }

    public SurveyResponse getSurvey(Long surveyId){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        // get survey

        SurveyResponse surveyDto = SurveyResponse.builder()
                .surveyId(survey.getId())
                .surveyType(survey.getSurveyType())
                .title(survey.getTitle())
                .content(survey.getContent())
                .build();
        // get question

       List<QuestionResponse> questionList = jpaQueryFactory
                .select(
                        Projections.constructor(QuestionResponse.class,
                                q.id,
                                q.surveyQuestion,
                                q.answerType,
                                q.score,
                                q.step,
                                q.isRequired,
                                Projections.list(
                                        Projections.constructor(AnswerResponse.class,
                                                a.id,
                                                a.surveyAnswer,
                                                a.step,
                                                a.correct
                                        )
                                )
                        )
                )
                .from(q)
                .leftJoin(a).on(q.id.eq(a.question.id)
                       .and(a.delFlag.isNull().or(a.delFlag.isFalse()))
               )
                .where(
                        q.survey.id.eq(survey.getId()),
                        q.delFlag.isFalse()
                )
                .groupBy(q.id, a.id)  // Group by questionId
                .orderBy(q.step.asc(), a.step.asc())
                .fetch();

        surveyDto.setQuestions(mergeAnswers(questionList));
        return surveyDto;
    }

    public void createSurvey(CreateSurveyRequest createSurveyRequest, Long workspaceId, LoginUser loginUser){
        Long surveyId = addSurvey(createSurveyRequest,  workspaceId, loginUser);
        List<CreateQuestionRequest> questionRequests = createSurveyRequest.getQuestions();
        addQuestions(questionRequests, surveyId);
    }


    public void updateSurvey(CreateSurveyRequest createSurveyRequest, Long surveyId, LoginUser loginUser){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        Long newSurveyId = modifySurvey(createSurveyRequest, loginUser, survey);
        addQuestions(createSurveyRequest.getQuestions(), newSurveyId);
    }

    public void deleteSurvey(Long surveyId){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        survey.setDelFlag(true);
        surveyRepository.save(survey);
    }


    // create survey
    private Long addSurvey(CreateSurveyRequest createSurveyRequest, Long workspaceId, LoginUser loginUser){
        User user = userRepository.findById(loginUser.getId()).orElseThrow();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();
        // save survey
        Survey survey = Survey.toEntity(user, workspace, createSurveyRequest);
        surveyRepository.save(survey);
        survey.setDelFlag(false);
        addGroupAndVersion(survey.getId(), 1L, survey);
        return survey.getId();
    }


    // update survey
    private Long modifySurvey(CreateSurveyRequest createSurveyRequest, LoginUser loginUser, Survey survey) {
        User user = userRepository.findById(loginUser.getId()).orElseThrow();
        Workspace workspace = survey.getWorkspace();
        // group , version
        Long group = survey.getSurveyGroup();
        Long version = survey.getVersion();
        Survey newSurvey = Survey.toEntity(user, workspace, createSurveyRequest);
        surveyRepository.save(newSurvey);
        newSurvey.setDelFlag(false);
        addGroupAndVersion(group, version+1, newSurvey);
        return newSurvey.getId();
    }

    private void addGroupAndVersion(Long group, Long version, Survey survey){
        survey.updateGroup(group);
        survey.updateVersion(version);
    }

    // create questions
    private void addQuestions(List<CreateQuestionRequest> createQuestionRequest, Long surveyId){
        Survey survey = findSurvey(surveyId);
        createQuestionRequest.forEach(createQuestionDto -> {
            Question question = Question.toEntity(createQuestionDto, survey);
            questionRepository.save(question);
            addAnswer(createQuestionDto.getAnswers(), question);
        });
    }

    // update questions
    private void modifyQuestions(List<UpdateQuestionRequest> updateQuestionRequest){
        updateQuestionRequest.forEach(updateQuestionDto -> {
            Question question = questionRepository.findById(updateQuestionDto.getQuestionId()).orElseThrow();
            question.updateQuestion(updateQuestionDto);
            question.setDelFlag(false);
            questionRepository.save(question);
            addAnswer(updateQuestionDto.getAnswers(), question);
        });
    }

    // update 전처리 -> delete question, answer

    private void deleteQuestionAndAnswer(Long surveyId) {
        List<Question> questionList =questionRepository.findAllBySurveyId(surveyId);
        questionList.forEach(question -> {
            // delete question
            question.setDelFlag(true);
            questionRepository.save(question);

            // delete answers
            List<Answer> answerList = answerRepository.findAllByQuestionIdAndDelFlagFalse(question.getId());
            answerList.forEach(answer -> {
                answer.setDelFlag(true);
                answerRepository.save(answer);
            });

        });
    }

    // create answers
    private void addAnswer(List<CreateAnswerRequest> createAnswerRequests, Question question){
        createAnswerRequests.forEach(answerDto -> {
            Answer answer = Answer.toEntity(answerDto, question);
            answerRepository.save(answer);
        });
    }


    public Survey findSurvey(Long surveyId){
        return surveyRepository.findById(surveyId).orElseThrow(
                () -> new SurveyException(SurveyExceptionType.NON_EXIST_SURVEY)
        );
    }


    private void checkAvailable(Survey survey){
        if (survey.getDelFlag()){
            throw new SurveyException(SurveyExceptionType.ALREADY_DELETED);
        }
    }


    private void checkPermission(LoginUser loginUser, Long workspaceId){
        User user = userRepository.findById(loginUser.getId()).orElseThrow();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();

        if (workspace.getWorkspaceType().equals(WorkspaceType.COMPANY)) {
            if (workspaceAdminRepository.findByDelFlagFalseAndWorkspaceIdAndUserId(user.getId(), workspace.getId()) == null) {
                throw new SurveyException(SurveyExceptionType.NO_PERMISSION);
            }
        }else {
            if(workspaceRepository.findByUserIdAndWorkspaceType(user.getId(), WorkspaceType.PERSONAL) == null){
                throw new SurveyException(SurveyExceptionType.NO_PERMISSION);
            }
        }
    }

    private List<QuestionResponse> mergeAnswers(List<QuestionResponse> questions) {
        Map<Long, QuestionResponse> questionMap = new HashMap<>();

        for (QuestionResponse question : questions) {
            Long questionId = question.getQuestionId();

            if (questionMap.containsKey(questionId)) {
                List<AnswerResponse> existingAnswers = new ArrayList<>(questionMap.get(questionId).getAnswers());
                existingAnswers.addAll(question.getAnswers());
                questionMap.get(questionId).setAnswers(existingAnswers);
            } else {
                questionMap.put(questionId, question);
            }
        }

        List<QuestionResponse> result =  new ArrayList<>(questionMap.values());
        result.sort(Comparator.comparing(QuestionResponse::getStep));

        for (QuestionResponse mergedQuestion : result) {
            List<AnswerResponse> mutableAnswers = new ArrayList<>(mergedQuestion.getAnswers());
            mutableAnswers.sort(Comparator.comparing(AnswerResponse::getStep));
            mergedQuestion.setAnswers(mutableAnswers);
        }

        return result;
    }


    private OrderSpecifier<?> sortByField(String fieldName){

        Order desc = Order.DESC;
        Order asc = Order.ASC;


        if (Objects.isNull(fieldName)){
            return new OrderSpecifier<>(desc, s.id);
        }

        if (fieldName.equals("regDate")){
            return new OrderSpecifier<>(asc, s.regDate);
        }

        if (fieldName.equals("modDate")){
            return new OrderSpecifier<>(desc, s.modDate);
        }

        return new OrderSpecifier<>(desc, s.id);
    }


}
