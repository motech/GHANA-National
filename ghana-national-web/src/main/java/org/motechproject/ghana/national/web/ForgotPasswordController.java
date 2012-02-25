package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.EmailGateway;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/forgotPassword")
public class ForgotPasswordController {
    private StaffService staffService;
    private EmailGateway emailGateway;

    public ForgotPasswordController() {
    }

    @Autowired
    public ForgotPasswordController(StaffService staffService, EmailGateway emailGateway) {
        this.staffService = staffService;
        this.emailGateway = emailGateway;
    }

    @LoginAsAdmin
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView changePasswordAndSendEmail(HttpServletRequest request) throws Exception {
        ModelAndView modelAndView = new ModelAndView("redirect:/forgotPasswordStatus.jsp");
        String emailId = request.getParameter("emailId");

        String newPassword = staffService.changePasswordByEmailId(emailId);
        if (!newPassword.equals("")) {
            String emailSentStatus = emailGateway.sendEmailUsingTemplates(emailId, newPassword);
            if (emailSentStatus.equals(Constants.EMAIL_SUCCESS))
                modelAndView.addObject(Constants.FORGOT_PASSWORD_MESSAGE, Constants.FORGOT_PASSWORD_SUCCESS);
            else
                modelAndView.addObject(Constants.FORGOT_PASSWORD_MESSAGE, Constants.FORGOT_PASSWORD_FAILURE);
        } else {
            modelAndView.addObject(Constants.FORGOT_PASSWORD_MESSAGE, Constants.FORGOT_PASSWORD_USER_NOT_FOUND);
        }

        return modelAndView;
    }


}
