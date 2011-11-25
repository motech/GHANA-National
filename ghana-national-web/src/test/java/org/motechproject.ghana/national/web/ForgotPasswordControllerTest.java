package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.service.EmailTemplateService;
import org.motechproject.ghana.national.service.UserService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ForgotPasswordControllerTest {

    private ForgotPasswordController forgotPasswordController;
    @Mock
    private EmailTemplateService emailTemplateService;
    @Mock
    private UserService userService;

    @Before
    public void setUp() {
        initMocks(this);
        forgotPasswordController = new ForgotPasswordController(userService, emailTemplateService);
    }

    @Test
    public void testChangePasswordAndSendEmailSuccessfully() throws Exception {
        ModelAndView expectedModelAndView = new ModelAndView("redirect:forgotPasswordStatus.jsp");
        final String newPassword = "newPassword";
        final String testEmailId = "a@a.com";

        HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);
        when(mockHttpRequest.getParameter("emailId")).thenReturn(testEmailId);
        when(userService.changePasswordByEmailId(testEmailId)).thenReturn(newPassword);
        when(emailTemplateService.sendEmailUsingTemplates(testEmailId, newPassword)).thenReturn(Constants.EMAIL_SUCCESS);
        ModelAndView actualModelAndView = forgotPasswordController.changePasswordAndSendEmail(mockHttpRequest);

        assertThat(actualModelAndView.getView(), is(equalTo(expectedModelAndView.getView())));
        String actualMessage = (String) actualModelAndView.getModel().get("message");
        assertThat(actualMessage, is(equalTo(Constants.FORGOT_PASSWORD_SUCCESS)));
    }

    @Test
    public void testChangePasswordAndDoNotSendEmailWhenUserNotFound() throws Exception {
        ModelAndView expectedModelAndView = new ModelAndView("redirect:forgotPasswordStatus.jsp");
        final String testEmailId = "a@a.com";

        HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);
        when(mockHttpRequest.getParameter("emailId")).thenReturn(testEmailId);
        when(userService.changePasswordByEmailId(testEmailId)).thenReturn("");
        ModelAndView actualModelAndView = forgotPasswordController.changePasswordAndSendEmail(mockHttpRequest);

        assertThat(actualModelAndView.getView(), is(equalTo(expectedModelAndView.getView())));
        String actualMessage = (String) actualModelAndView.getModel().get("message");
        assertThat(actualMessage, is(equalTo(Constants.FORGOT_PASSWORD_USER_NOT_FOUND)));

    }

}
