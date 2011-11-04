package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Email;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import javax.mail.Session;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;

public class EmailServiceTest {

    private EmailService emailService;
    @Mock
    private JavaMailSender mockMailSender;
    @Mock
    private Email mockEmail;

    @Before
    public void init() {
        initMocks(this);
        mockMailSender = mock(JavaMailSender.class);
        mockEmail = mock(Email.class);
        emailService = new EmailService(mockMailSender);
    }

    @Test
    public void shouldSendEmail() {
        final String toAddress = "to@to.com";
        final String fromAddress = "from@from.com";
        final String subject = "Test - Email subject";
        final String emailText = "Test - Email text";

        when(mockEmail.to()).thenReturn(toAddress);
        when(mockEmail.from()).thenReturn(fromAddress);
        when(mockEmail.subject()).thenReturn(subject);
        when(mockEmail.text()).thenReturn(emailText);

        String emailSentStatus = emailService.send(mockEmail);

        final ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender).send(captor.capture());

        final SimpleMailMessage simpleMailMessage = captor.getValue();

        assertThat(emailSentStatus,is(equalTo(Constants.EMAIL_SUCCESS)));

        assertThat(simpleMailMessage.getText(), is(equalTo(emailText)));
        assertThat(simpleMailMessage.getFrom(), is(equalTo(fromAddress)));
        assertThat(simpleMailMessage.getTo()[0].toString(), is(equalTo(toAddress)));
        assertThat(simpleMailMessage.getSubject(), is(equalTo(subject)));
    }

    @Test
    public void shouldSendFailureMessageIfEmailNotSent(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setSession(getSession());
        emailService = new EmailService(mailSender);

        when(mockEmail.to()).thenReturn(null);
        when(mockEmail.from()).thenReturn(null);
        when(mockEmail.subject()).thenReturn(null);
        when(mockEmail.text()).thenReturn(null);

        String emailSentStatus = emailService.send(mockEmail);

        assertThat(emailSentStatus,is(equalTo(Constants.EMAIL_FAILURE)));
    }
    private Session getSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "10.101.101.101");
		properties.setProperty("mail.smtp.port", "33");
            properties.setProperty("mail.smtp.starttls.enable","true");
            properties.setProperty("mail.smtp.auth", "true");
        return Session.getInstance(properties);
    }
}
