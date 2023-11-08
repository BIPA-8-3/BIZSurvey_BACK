package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.dao.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.MailAuthRequest;
import com.bipa.bizsurvey.domain.user.dto.RequestJoinDto;
import com.bipa.bizsurvey.domain.user.dto.ResponseJoinDto;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;
    
    public ResponseJoinDto join(RequestJoinDto joinDto){
        Optional<User> userOptional = userRepository.findByEmail(joinDto.getEmail());

        if(userOptional.isPresent()){
            throw new UserException(UserExceptionType.ALREADY_EXIST_EMAIL);
        }

        User user = userRepository.save(joinDto.toEntity(passwordEncoder));
        return new ResponseJoinDto(user);
    }

    // 닉네임 중복 확인
    public boolean nickNameCheck(String nickname){
       // Optional<User> nicknameUser = userRepository.findByNickname(nickname);
        //return nicknameUser.isPresent();
        return true;
    }

    public void authEmail(String email){

        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        sendAuthEmail(email, authKey);
    }

    public void sendAuthEmail(String email, String authKey) {
 
        String subject = "[bizSurvey] 인증번호";
        String text = "회원 가입을 위한 인증번호는 " + authKey + "입니다. <br/>";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);//포함된 텍스트가 HTML이라는 의미로 true.
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        setDataExpire(authKey, email, 60 * 3L);
    }


    //사용자가 입력한 인증 체크
    public boolean authCheck(MailAuthRequest request){
        String value = getData(request.getAuthNumber());
        return value != null && value.equals(request.getMail());
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