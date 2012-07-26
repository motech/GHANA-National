package org.motechproject.ghana.national.repository;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Repository
public class EmailGateway {

    private VelocityEngine velocityEngine;

    @Value("#{emailProperties['from.address']}")
    private String motechFromAddress;

    @Value("#{emailProperties['mailSubjectTemplate']}")
    private String subjectTemplate;

    @Value("#{emailProperties['mailTextTemplate']}")
    private String textTemplate;

    @Value("#{emailProperties['smtp.username']}")
    private String username;

    @Value("#{emailProperties['smtp.password']}")
    private String password;

    @Value("#{emailProperties['smtp.host']}")
    private String host;

    @Value("#{emailProperties['smtp.port']}")
    private String port;

    @Value("#{emailProperties['smtp.socket.factory.port']}")
    private String socketFactoryPort;

    @Value("#{emailProperties['smtp.socket.factory.class']}")
    private String socketFactoryClass;

    @Value("#{emailProperties['smtp.auth']}")
    private String authEnabled;

    @Autowired
    public EmailGateway(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    int send(Email email) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", socketFactoryPort);
        props.put("mail.smtp.socketFactory.class", socketFactoryClass);
        props.put("mail.smtp.auth", authEnabled);
        props.put("mail.smtp.port", port);

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        int mailSentStatus;
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.from()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.to()));
            message.setSubject(email.subject());
            message.setText(email.text());
            Transport.send(message);
            mailSentStatus = Constants.EMAIL_SUCCESS;
        } catch (Exception e) {
            mailSentStatus = Constants.EMAIL_FAILURE;

        }
        return mailSentStatus;
    }

    public int sendEmailUsingTemplates(final String userName, final String password) {
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
