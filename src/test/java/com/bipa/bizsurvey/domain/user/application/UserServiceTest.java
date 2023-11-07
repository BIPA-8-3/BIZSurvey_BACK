package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.dao.UserRepository;
import com.bipa.bizsurvey.domain.user.dto.RequestJoinDto;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    public void join() throws Exception{
        RequestJoinDto user = new RequestJoinDto();
        user.setEmail("404444@naver.com");
        user.setName("hong");
        user.setNickname("honeNick");
        user.setGender(Gender.FEMALE);
        user.setBirthdate("20220908");
        user.setPassword("qkrthdud6032!");

        String saveEmail = userService.join(user).getEmail();
        assertNotNull(saveEmail);
        assertEquals(user.getEmail(), saveEmail);
    }

    @Test
    public void duplicateEmail() throws Exception{
        RequestJoinDto user = new RequestJoinDto();
        user.setEmail("404444@naver.com");
        user.setName("hong");
        user.setNickname("honeNick");
        user.setGender(Gender.FEMALE);
        user.setBirthdate("20220908");
        user.setPassword("password123!");

        userService.join(user);

        RequestJoinDto user2 = new RequestJoinDto();
        user2.setEmail("404444@naver.com");
        user2.setName("hong");
        user2.setNickname("honeNick");
        user2.setGender(Gender.FEMALE);
        user2.setBirthdate("20220908");
        user2.setPassword("qkrthdud6032!");

        assertThrows(UserException.class, () -> userService.join(user2));
    }
}