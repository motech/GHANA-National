package org.motechproject.ghana.national.service;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class EmailTemplateServiceIT extends BaseIntegrationTest {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Test
    @Ignore("This test is an integration test to be tested once all the smtp setup is done")
    public void shouldSendEmailWithTemplates() {

        final String toEmailId = "karthis@thoughtworks.com";
        final String password = "abcd1234";

        String emailSentStatus = emailTemplateService.sendEmailUsingTemplates(toEmailId, password);
        assertThat(emailSentStatus,is(equalTo(Constants.EMAIL_SUCCESS)));
    }
}
