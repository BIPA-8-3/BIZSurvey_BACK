package com.bipa.bizsurvey.global.common;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Log4j2
public class BaseEntityTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() {
        User user = User.builder()
                .email("test@naver.com")
                .nickname("unique?")
                .name("테스트")
                .birthdate("birthdate")
                .planSubscribe(Plan.NORMAL_SUBSCRIBE)
                .build();
     userRepository.save(user);
     log.info(user);
    }
}
