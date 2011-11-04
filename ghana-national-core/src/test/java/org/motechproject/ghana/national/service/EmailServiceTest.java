package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

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

        emailService.send(mockEmail);

        final ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender).send(captor.capture());

        final SimpleMailMessage simpleMailMessage = captor.getValue();

        assertThat(simpleMailMessage.getText(), is(equalTo(emailText)));
        assertThat(simpleMailMessage.getFrom(), is(equalTo(fromAddress)));
        assertThat(simpleMailMessage.getTo()[0].toString(), is(equalTo(toAddress)));
        assertThat(simpleMailMessage.getSubject(), is(equalTo(subject)));
    }
}
