package org.motechproject.ghana.national.repository;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Email;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailGatewayTest {

    private EmailGateway emailGateway;
    @Mock
    private Email mockEmail;
    @Mock
    private VelocityEngine mockVelocityEngine;

    @Before
    public void init() {
        initMocks(this);
        emailGateway = new EmailGateway( mockVelocityEngine);
        ReflectionTestUtils.setField(emailGateway, "username", "");
        ReflectionTestUtils.setField(emailGateway, "password", "");
        ReflectionTestUtils.setField(emailGateway, "host", "smtp.mailinator.com");
        ReflectionTestUtils.setField(emailGateway, "port", "25");
        ReflectionTestUtils.setField(emailGateway, "socketFactoryPort", "465");
        ReflectionTestUtils.setField(emailGateway, "socketFactoryClass", "javax.net.ssl.SSLSocketFactory");
        ReflectionTestUtils.setField(emailGateway, "authEnabled", "true");
        ReflectionTestUtils.setField(emailGateway, "motechFromAddress", "admin@motech.com");
        ReflectionTestUtils.setField(emailGateway, "subjectTemplate", "emailtemplates/mail_subject.vm");
        ReflectionTestUtils.setField(emailGateway, "textTemplate", "emailtemplates/mail_text.vm");
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

        int emailSentStatus = emailGateway.send(mockEmail);
        assertThat(emailSentStatus, is(equalTo(Constants.EMAIL_SUCCESS)));
    }

    @Test
    public void shouldSendFailureMessageIfEmailNotSent() throws Exception {
        final String fromAddress = "from@from.com";
        final String subject = "Test - Email subject";
        final String emailText = "Test - Email text";

        when(mockEmail.from()).thenReturn(fromAddress);
        when(mockEmail.subject()).thenReturn(subject);
        when(mockEmail.text()).thenReturn(emailText);

        int emailSentStatus = emailGateway.send(mockEmail);

        assertThat(emailSentStatus, is(equalTo(Constants.EMAIL_FAILURE)));
    }
}
