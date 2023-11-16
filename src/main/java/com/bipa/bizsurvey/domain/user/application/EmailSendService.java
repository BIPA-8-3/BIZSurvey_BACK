package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.EmailCheckRequest;
import com.bipa.bizsurvey.domain.user.dto.MailAuthRequest;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.global.common.RedisService;
import lombok.RequiredArgsConstructor;
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

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    public void authEmail(EmailCheckRequest request){
        Optional<User> emailUser = userRepository.findByEmail(request.getEmail());
        if(emailUser.isPresent()){
            throw new UserException(UserExceptionType.ALREADY_EXIST_EMAIL);
        }
        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);
        sendAuthEmail(request.getEmail(), authKey);
    }

    public void checkEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserException(UserExceptionType.NON_EXIST_EMAIL)
        );

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
        redisService.saveData(authKey, email, 60 * 3L);
    }

    //사용자가 입력한 인증 번호 체크
    public void authCheck(MailAuthRequest request){
        String value = redisService.getData(request.getAuthNumber());
        if (value == null || !value.equals(request.getEmail())) {
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
        String text = "<div>"
                + "<p>아래 링크를 클릭하면 비밀번호를 재설정하세요. <br>"
                + "(이 링크를 24시간 후 만료되며 한 번만 사용할 수 있습니다.)<p>"
                + "<a href='http://localhost:8080/email-validation/" + key + "'>인증 링크</a>"
                + "</div>";
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
        redisService.saveData(key, email, 60L);
    }

    // 비밀번호 재전송 링크가 만료 됬는치 체크(만료가 아니면 email 리턴)
    public String emailValidation(String key){
        String value = redisService.getData(key);
        if (value == null) {
            throw new UserException(UserExceptionType.NON_AUTH_PASSWORDEMAIL);
        }
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
