package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.dao.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.EmailCheckRequest;
import com.bipa.bizsurvey.domain.user.dto.MailAuthRequest;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailSendService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisTemplate redisTemplate;

    public void authEmail(EmailCheckRequest request){

        Optional<User> emailUser = userRepository.findByEmail(request.getEmail());

        if(emailUser.isPresent()){
            throw new UserException(UserExceptionType.ALREADY_EXIST_EMAIL);
        }

        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        sendAuthEmail(request.getEmail(), authKey);
    }

    public void sendAuthEmail(String email, String authKey) {

        String subject = "[bizSurvey] 인증번호";
        String text = "회원 가입을 위한 인증번호는 " + authKey + "입니다. <br/>";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        setDataExpire(authKey, email, 60 * 3L);
    }


    //사용자가 입력한 인증 체크
    public void authCheck(MailAuthRequest request){
        String value = getData(request.getAuthNumber());
        if (value == null || !value.equals(request.getEmail())) {
            throw new UserException(UserExceptionType.ALREADY_EXIST_AUTH_NUMBER);
        }
    }


    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }
}
