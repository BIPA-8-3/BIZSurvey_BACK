package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.EmailCheckRequest;
import com.bipa.bizsurvey.domain.user.dto.MailAuthRequest;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.global.common.RedisService;
import com.bipa.bizsurvey.global.common.email.EmailMessage;
import com.bipa.bizsurvey.global.common.email.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Random;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailSendService {

    //
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;
    private final MailUtil mailUtil;

    @Value("${spring.domain.frontend}")
    private String front;
    public void authEmail(EmailCheckRequest request) throws Exception {
        Optional<User> emailUser = userRepository.findByEmail(request.getEmail());
        if(emailUser.isPresent()){
            throw new UserException(UserExceptionType.ALREADY_EXIST_EMAIL);
        }

        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        try {
            sendAuthEmail(request.getEmail(), authKey);
        } catch (Exception e) {
            throw new Exception("인증 이메일 전송 중 오류 발생", e);
        }
    }

    public void checkEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserException(UserExceptionType.NON_EXIST_EMAIL)
        );
        if(user.getProvider().equals("kakao")){
            throw new UserException(UserExceptionType.KAKAO_PROVIDER_CHECK);
        }
    }

    public void sendAuthEmail(String email, String authKey) throws Exception {
        String subject = "[bizSurvey] 인증번호";
        String text = "회원 가입을 위한 인증번호는 " + authKey + "입니다.";
        System.out.println(email);
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject(subject)
                .build();


        emailMessage.put("msg", text);
        emailMessage.put("hasLink", true);

        mailUtil.sendTemplateMail(emailMessage);
        redisService.saveData(authKey, email, 60 * 60 * 24L);
    }

    //사용자가 입력한 인증 번호 체크
    public void authCheck(MailAuthRequest request){
        String value = redisService.getData(request.getAuthNumber());
        if (value == null) {
            throw new UserException(UserExceptionType.ALREADY_EXIST_AUTH_NUMBER);
        }
        String email = value.replace("\"","");
        System.out.println(email);
        System.out.println(request.getAuthNumber());
        if (!email.equals(request.getEmail())) {
            throw new UserException(UserExceptionType.ALREADY_EXIST_AUTH_NUMBER);
        }
    }

    //비밀번호 재설정 이메일 전송(이메일 암호화 및 1일 저장)
    public void sendPasswordEmail(String email) throws Exception {
        SecretKey secretKey = generateAESKey();
        // 암호화
        byte[] encryptedBytes = encrypt(email, secretKey);
        String key = Base64.getUrlEncoder().encodeToString(encryptedBytes);
        String subject = "[bizSurvey] 비밀번호 재설정";
        String text = "아래 링크를 클릭하면 비밀번호를 재설정하세요."
                + "(이 링크를 24시간 후 만료되며 한 번만 사용할 수 있습니다.)";

        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject(subject)
                .build();

        emailMessage.put("msg", text);
        emailMessage.put("hasLink", true);
        emailMessage.put("link", front +"/emailValidation/" + key );
        emailMessage.put("linkText", "비밀번호 변경");
        mailUtil.sendTemplateMail(emailMessage);

        redisService.saveData(key, email, 60L);
    }

    // 비밀번호 재전송 링크가 만료 됬는치 체크(만료가 아니면 email 리턴)
    public String emailValidation(String key){
        String value = redisService.getData(key);
        if (value == null) {
            throw new UserException(UserExceptionType.NON_AUTH_PASSWORDEMAIL);
        }

        //redis에서 삭제
        return value;
    }

    private SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    private byte[] encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText.getBytes());
    }

    private String decrypt(byte[] cipherText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes);
    }

}
