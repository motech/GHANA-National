package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender ;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    public void send(Email email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.to());
        mailMessage.setFrom(email.from());
        mailMessage.setSubject(email.subject());
        mailMessage.setText(email.text());
        mailSender.send(mailMessage);
    }
}
