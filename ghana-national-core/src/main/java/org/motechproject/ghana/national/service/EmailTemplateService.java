package org.motechproject.ghana.national.service;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.ghana.national.domain.Email;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

public class EmailTemplateService {

    private String motechFromAddress;
    private String subjectTemplate;
    private String textTemplate;

    private VelocityEngine velocityEngine;
    private EmailService emailService;

    public EmailTemplateService(String motechFromAddress, String subjectTemplate, String textTemplate) {
        this.motechFromAddress = motechFromAddress;
        this.subjectTemplate = subjectTemplate;
        this.textTemplate = textTemplate;
    }

    public String sendEmailUsingTemplates(final String userName, final String password) {
        Map<String, String> emailData = new HashMap<String, String>() {{
            put("userName", userName);
            put("password", password);

        }};
        return emailService.send(new Email(userName, motechFromAddress, renderWith(subjectTemplate, emailData), renderWith(textTemplate, emailData)));
    }

    private String renderWith(String template, Map data) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, data).trim();
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
