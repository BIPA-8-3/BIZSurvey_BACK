package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.survey.domain.*;
import com.bipa.bizsurvey.domain.survey.dto.request.*;
import com.bipa.bizsurvey.domain.survey.dto.response.AnswerResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.QuestionResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyListResponse;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType;
//import com.bipa.bizsurvey.domain.survey.mapper.SurveyMapper;
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
import com.bipa.bizsurvey.global.common.sorting.OrderByNull;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
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
//    private final SurveyMapper surveyMapper;
    private final UserRepository userRepository;
    private final WorkspaceAdminRepository workspaceAdminRepository;
    private final JPAQueryFactory jpaQueryFactory;
    public QSurvey s = new QSurvey("s");
    public QQuestion q = new QQuestion("q");
    public QAnswer a = new QAnswer("a");

    public List<SurveyListResponse> getSurveyList(Long workspaceId, String fieldName){
        return jpaQueryFactory
                .select(Projections.constructor(SurveyListResponse.class, s.id, s.title, s.surveyType))
                .from(s)
                .where(s.workspace.id.eq(workspaceId)
                        .and(s.delFlag.eq(false)))
                .orderBy(sortByField(fieldName))
                .fetch();
    }

    public SurveyResponse getSurvey(Long surveyId){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        // get survey
//        SurveyResponse surveyDto = surveyMapper.toSurveyInWorkspaceResponse(survey);

        SurveyResponse surveyDto = SurveyResponse.builder()
                .surveyId(survey.getId())
                .surveyType(survey.getSurveyType())
                .title(survey.getTitle())
                .content(survey.getContent())
                .build();
        // get question
        Long surveyKey = survey.getId();
//        List<QuestionResponse> questionDtoList = surveyMapper
//                .toQuestionInWorkspaceResponseList(questionRepository.findAllBySurveyIdAndDelFlagFalseOrderByStep(surveyKey));

        // get answer
//        questionDtoList.forEach(questionDto -> {
//                Long questionKey = questionDto.getQuestionId();
//                List<AnswerResponse> answerDtoList = surveyMapper
//                        .toAnswerInWorkspaceResponseList(answerRepository.findAllByQuestionIdAndDelFlagFalseOrderByStep(questionKey));
//                questionDto.setAnswers(answerDtoList);
//        });
//
//        surveyDto.setQuestions(questionDtoList);

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
                .leftJoin(a).on(q.id.eq(a.question.id))
                .where(
                        q.survey.id.eq(survey.getId()),
                        q.delFlag.isFalse(),
                        a.delFlag.isFalse().or(a.delFlag.isNull())
                )
                .groupBy(q.id, a.id)  // Group by questionId
                .orderBy(q.step.asc(), a.step.asc())
                .fetch();

        surveyDto.setQuestions(mergeAnswers(questionList));
        return surveyDto;
    }

    public void createSurvey(CreateSurveyRequest createSurveyRequest, Long workspaceId, LoginUser loginUser){
//        checkPermission(loginUser, workspaceId);
        Long surveyId = addSurvey(createSurveyRequest,  workspaceId, loginUser);
        List<CreateQuestionRequest> questionRequests = createSurveyRequest.getQuestions();
        // 질문 수 체크
        if (questionRequests.size() > 40){
            throw new SurveyException(SurveyExceptionType.QUESTION_LIMIT_EXCEEDED);
        }
        addQuestions(questionRequests, surveyId);

    }


    public void updateSurvey(UpdateSurveyRequest updateSurveyRequest, Long surveyId){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        modifySurvey(updateSurveyRequest);
        deleteQuestionAndAnswer(surveyId);
        modifyQuestions(updateSurveyRequest.getUpdateQuestions());
        addQuestions(updateSurveyRequest.getCreateQuestions(), surveyId);
    }

    public void deleteSurvey(Long surveyId){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        survey.setDelFlag(true);
        surveyRepository.save(survey);
    }


    // create survey & check permission
    private Long addSurvey(CreateSurveyRequest createSurveyRequest, Long workspaceId, LoginUser loginUser){
        User user = userRepository.findById(loginUser.getId()).orElseThrow();

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();
        // save survey
        Survey survey = Survey.toEntity(user, workspace, createSurveyRequest);
        surveyRepository.save(survey);
        return survey.getId();
    }


    // update survey
    private void modifySurvey(UpdateSurveyRequest updateSurveyRequest) {
        Survey survey = findSurvey(updateSurveyRequest.getSurveyId());
        survey.updateSurvey(updateSurveyRequest);
        surveyRepository.save(survey);
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
            if (workspaceAdminRepository.findByWorkspaceIdAndUserId(user.getId(), workspace.getId()) == null) {
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
