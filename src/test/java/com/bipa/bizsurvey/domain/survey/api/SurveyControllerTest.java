package com.bipa.bizsurvey.domain.survey.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
class SurveyControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetSurvey() throws Exception{


        mockMvc.perform(MockMvcRequestBuilders.get("/survey/{surveyId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surveyId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.questions[0].questionId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.questions[0].answers[0].answerId").value(1L));



    }


}