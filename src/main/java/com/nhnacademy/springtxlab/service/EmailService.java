package com.nhnacademy.springtxlab.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String SUBJECT = "TEST";
    private static final String CONTENT = "TEST CONTENT";
    private final JavaMailSender mailSender;


    public void sendEmail(String to) throws MessagingException, MailException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(SUBJECT);
        helper.setText(CONTENT);

        mailSender.send(mimeMessage);
    }
}
