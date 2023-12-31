package com.bipa.bizsurvey.global.common.email;

import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MailUtil {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine htmlTemplateEngine;

    @Value("${AES.PRIVATE_KEY}")
    private String private_key;



    public void sendTemplateMail(EmailMessage emailMessage) throws Exception {
        Context context = new Context();

        InputStream imageStream = getClass().getClassLoader().getResourceAsStream("static/img/logo.png");

        MimeType mimeType = MimeTypeUtils.parseMimeType("image/png");

        context.setVariable("logoImage", "cid:logoImage");
        context.setVariables(emailMessage.getVariables());

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setTo(emailMessage.getTo());
        messageHelper.setSubject(emailMessage.getSubject());

        String htmlTemplate = htmlTemplateEngine.process("mail/mail", context);
        messageHelper.setText(htmlTemplate, true);

        // InputStream을 ByteArray로 변환하여 Resource 생성
        byte[] imageBytes = IOUtils.toByteArray(imageStream);
        Resource imageResource = new ByteArrayResource(imageBytes);

        // Content ID로 이미지 추가
        messageHelper.addInline("logoImage", imageResource, mimeType.toString());

        javaMailSender.send(mimeMessage);

//        Context context = new Context();
//
//        ClassPathResource imageResource = new ClassPathResource("/static/img/logo.png");
//        String imagePath = imageResource.getFile().getAbsolutePath();
//
//        context.setVariable("logoImage", imagePath);
//
//        context.setVariables(emailMessage.getVariables());
//
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//        messageHelper.setTo(emailMessage.getTo());
//        messageHelper.setSubject(emailMessage.getSubject());
//
//        String htmlTemplate = htmlTemplateEngine.process("mail/mail", context);
//        messageHelper.setText(htmlTemplate, true);
//        messageHelper.addInline("logoImage", new File(imagePath));
//
//        javaMailSender.send(mimeMessage);
    }

    public void sendTemplateGroupMail(List<EmailMessage> emailMessageList) throws Exception {
        for (EmailMessage emailMessage : emailMessageList) {
            sendTemplateMail(emailMessage);
        }
    }

    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, new SecureRandom());
        return keyGenerator.generateKey();
    }

    public String encrypt(String plainText) throws Exception {
        byte[] decodedKeyBytes = Base64.getDecoder().decode(private_key);
        SecretKey secretKey = new SecretKeySpec(decodedKeyBytes, "AES");
        return Base64.getUrlEncoder().encodeToString(encrypt(plainText, secretKey));
    }

    public byte[] encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    public String decrypt(String cipherText) throws Exception {
        byte[] decodedKeyBytes = Base64.getDecoder().decode(private_key);
        byte[] cipherBytes = Base64.getUrlDecoder().decode(cipherText);
        SecretKey secretKey = new SecretKeySpec(decodedKeyBytes, "AES");
        return decrypt(cipherBytes, secretKey);
    }

    public String decrypt(byte[] cipherText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes, "UTF-8");
    }
}
