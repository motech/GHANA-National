package org.motechproject.ghana.national.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class EmailTemplateServiceIT extends AbstractJUnit4SpringContextTests {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Test
    @Ignore("This test is an integration test to be tested once all the smtp setup is done")
    public void shouldSendEmailWithTemplates() {

        final String toEmailId = "karthis@thoughtworks.com";
        final String staffId = "123456789";
        final String password = "abcd1234";

        emailTemplateService.sendEmailUsingTemplates(toEmailId, staffId, password);
    }

}
