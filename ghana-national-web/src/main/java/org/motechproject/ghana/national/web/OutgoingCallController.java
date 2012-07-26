package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.repository.IVRGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/outgoing")
public class OutgoingCallController {

    @Autowired
    IVRGateway ivrGateway;

    public OutgoingCallController() {
    }

    @RequestMapping("/call")
    @ResponseBody
    public String call(HttpServletRequest request) {
        String phoneNumber = request.getParameter("callerid");
        if (phoneNumber.trim() != null)
            ivrGateway.placeCall(phoneNumber, null);
        return "";
    }
}
