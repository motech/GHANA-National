package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.EmailGateway;
import org.motechproject.ghana.national.service.StaffService;
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
    private EmailGateway mockEmailGateway;
    @Mock
    private StaffService staffService;

    @Before
    public void setUp() {
        initMocks(this);
        forgotPasswordController = new ForgotPasswordController(staffService);
    }

    @Test
    public void testChangePasswordAndSendEmailSuccessfully() throws Exception {
        ModelAndView expectedModelAndView = new ModelAndView("redirect:forgotPasswordStatus.jsp");
        final String testEmailId = "a@a.com";

        HttpServletRequest mockHttpRequest = mock(HttpServletRequest.class);
        when(mockHttpRequest.getParameter("emailId")).thenReturn(testEmailId);
        when(staffService.changePasswordByEmailId(testEmailId)).thenReturn(Constants.EMAIL_SUCCESS);
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
        when(staffService.changePasswordByEmailId(testEmailId)).thenReturn(Constants.EMAIL_USER_NOT_FOUND);
        ModelAndView actualModelAndView = forgotPasswordController.changePasswordAndSendEmail(mockHttpRequest);

        assertThat(actualModelAndView.getView(), is(equalTo(expectedModelAndView.getView())));
        String actualMessage = (String) actualModelAndView.getModel().get("message");
        assertThat(actualMessage, is(equalTo(Constants.FORGOT_PASSWORD_USER_NOT_FOUND)));

    }

}
