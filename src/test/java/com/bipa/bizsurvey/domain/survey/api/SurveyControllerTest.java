package com.bipa.bizsurvey.domain.survey.api;

import com.bipa.bizsurvey.domain.survey.dto.survey.CreateAnswerRequest;
import com.bipa.bizsurvey.domain.survey.dto.survey.CreateQuestionRequest;
import com.bipa.bizsurvey.domain.survey.dto.survey.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.survey.enums.Correct;
import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
class SurveyControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SurveyController surveyController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetSurvey() throws Exception{


        mockMvc.perform(MockMvcRequestBuilders.get("/survey/{surveyId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surveyId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.questions[0].questionId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.questions[0].answers[0].answerId").value(1L));

    }


    @Test
    public void testCreateSurvey() throws Exception{

        mockMvc = MockMvcBuilders.standaloneSetup(surveyController).build();

        CreateSurveyRequest createSurveyRequest = new CreateSurveyRequest("testtitle", "testcontent", SurveyType.NORMAL, null);
        List<CreateQuestionRequest> qlist = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CreateQuestionRequest createQuestionRequest = new CreateQuestionRequest("testQuestion", AnswerType.SINGLE_CHOICE, 0, null);
            List<CreateAnswerRequest> alist = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                CreateAnswerRequest createAnswerRequest = new CreateAnswerRequest("testAnswer", Correct.NO);
                alist.add(createAnswerRequest);
            }
            createQuestionRequest.setAnswers(alist);
            qlist.add(createQuestionRequest);
        }


        mockMvc.perform(MockMvcRequestBuilders.post("/survey")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSurveyRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("설문지 등록이 완료되었습니다."));


    }


}