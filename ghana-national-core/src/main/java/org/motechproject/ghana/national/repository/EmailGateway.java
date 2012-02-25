package org.motechproject.ghana.national.repository;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

public class EmailGateway {

    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;

    @Value("#{emailProperties['motechAdminFromAddress']}")
    private String motechFromAddress;

    @Value("#{emailProperties['mailSubjectTemplate']}")
    private String subjectTemplate;

    @Value("#{emailProperties['mailTextTemplate']}")
    private String textTemplate;

    @Autowired
    public EmailGateway(JavaMailSender javaMailSender, VelocityEngine velocityEngine) {
        this.mailSender = javaMailSender;
        this.velocityEngine = velocityEngine;
    }

    String send(Email email) {
        String mailSentStatus;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.to());
        mailMessage.setFrom(email.from());
        mailMessage.setSubject(email.subject());
        mailMessage.setText(email.text());
        try {
            mailSender.send(mailMessage);
            mailSentStatus = Constants.EMAIL_SUCCESS;
        }
        catch (Exception e) {
            mailSentStatus = Constants.EMAIL_FAILURE;
        }
        return mailSentStatus;
    }

    public String sendEmailUsingTemplates(final String userName, final String password) {
        Map<String, String> emailData = new HashMap<String, String>() {{
            put("userName", userName);
            put("password", password);

        }};
        return send(new Email(userName, motechFromAddress, renderWith(subjectTemplate, emailData), renderWith(textTemplate, emailData)));
    }

    private String renderWith(String template, Map data) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, data).trim();
    }
}
