package com.rest.documentManager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    public void sendConfirmationRegisterAccount(String destiny, String code) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        Context context = new Context();

        context.setVariable("title", "Welcome to Mark Sprites!");
        context.setVariable("text", "We need you to confirm your account...");
        context.setVariable("link", "http://localhost:8081/user/public/confirmation/" + code);

        String html = springTemplateEngine.process("email/confirmation", context);
        helper.setTo(destiny);
        helper.setText(html, true);
        helper.setSubject("Confirmation register");
        helper.setFrom("nao-response@markin.com.br");

        helper.addInline("logo", new ClassPathResource("/static/images/logo.png"));

        javaMailSender.send(message);
    }

    public void sendEmailConfirmationChangePassword(String destiny, String codeVerify) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        Context context = new Context();

        context.setVariable("title", "Change Password!");
        context.setVariable("text", "\n" +
                "You need to confirm your email to change your password...");
        context.setVariable("link", "http://localhost:8081/user/public/changedPassword/" + codeVerify);

        String html = springTemplateEngine.process("email/confirmation", context);
        helper.setTo(destiny);
        helper.setText(html, true);
        helper.setSubject("Confirmation change password");
        helper.setFrom("nao-response@markin.com.br");

        helper.addInline("logo", new ClassPathResource("/static/images/logo.png"));

        javaMailSender.send(message);
    }

    public void sendNoticeToChangePassword(String destiny) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        Context context = new Context();

        context.setVariable("title", "Password Changed!");
        context.setVariable("text", "\n" +
                "You changed your password on our website...");
        context.setVariable("link", "http://localhost:8081/");

        String html = springTemplateEngine.process("email/confirmation", context);
        helper.setTo(destiny);
        helper.setText(html, true);
        helper.setSubject("Notice password change");
        helper.setFrom("nao-response@markin.com.br");

        helper.addInline("logo", new ClassPathResource("/static/images/logo.png"));

        javaMailSender.send(message);
    }
}
