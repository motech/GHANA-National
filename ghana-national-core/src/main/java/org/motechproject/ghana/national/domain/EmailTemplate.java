package org.motechproject.ghana.national.domain;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

public class EmailTemplate {
      private VelocityEngine velocityEngine;

    private String emailTemplate;
    private String textTemplate;

    public EmailTemplate(String subjectTemplate, String textTemplate) {
        this.emailTemplate = subjectTemplate;
        this.textTemplate = textTemplate;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String emailData(Map data) {
        return renderWith(emailTemplate,data);
    }


    private String renderWith(String template, Map data) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, data).trim();
    }
}
