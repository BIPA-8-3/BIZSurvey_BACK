package com.bipa.bizsurvey.global.common.email;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
@RequiredArgsConstructor
public class MailUtil {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine htmlTemplateEngine;

    public void sendTemplateMail(EmailMessage emailMessage) throws Exception{
        Context context = new Context();

        ClassPathResource imageResource = new ClassPathResource("/static/img/logo.png");
        String imagePath = imageResource.getFile().getAbsolutePath();

        context.setVariable("logoImage", imagePath);

        context.setVariables(emailMessage.getVariables());

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setTo(emailMessage.getTo());
        messageHelper.setSubject(emailMessage.getSubject());

        String htmlTemplate = htmlTemplateEngine.process("mail/mail", context);
        messageHelper.setText(htmlTemplate, true);
        messageHelper.addInline("logoImage", new File(imagePath));

        javaMailSender.send(mimeMessage);
    }
}
