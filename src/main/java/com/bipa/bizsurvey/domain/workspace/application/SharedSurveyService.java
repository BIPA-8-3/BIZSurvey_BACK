package com.bipa.bizsurvey.domain.workspace.application;

import com.bipa.bizsurvey.domain.workspace.repository.SharedListRepository;
import com.bipa.bizsurvey.domain.workspace.repository.SharedSurveyRepository;
import com.bipa.bizsurvey.domain.workspace.repository.SharedSurveyResponseRepository;
import com.bipa.bizsurvey.global.common.email.EmailMessage;
import com.bipa.bizsurvey.global.common.email.MailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class SharedSurveyService {
    private final SharedSurveyRepository sharedSurveyRepository;
    private final SharedSurveyResponseRepository sharedSurveyResponseRepository;
    private final SharedListRepository sharedListRepository;
    private final MailUtil mailUtil;
    public void share() {
        /*
                링크 발행 후 메일로 전송
         */
    }

    public void surveyStatistics() {

    }

    public void readOne() {

    }



    public void create() {

    }
}
