package com.bipa.bizsurvey.infra.mail;

import com.bipa.bizsurvey.global.common.email.EmailMessage;
import com.bipa.bizsurvey.global.common.email.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class MailTest {
    @Autowired
    MailUtil mailUtils;


    @Test
    public void testSendMail() {
        Map<String, Object> variable = new HashMap<>();
        variable.put("msg", "불법 광고로 \"정지 3일\" 패널티를 받으셨습니다.");

        EmailMessage message = EmailMessage.builder()
                        .to("hws6745@naver.com")
                        .subject("[비즈서베이] 신고 사항 전달합니다")
                        .variables(variable)
                .build();
        try {
            mailUtils.sendTemplateMail(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
