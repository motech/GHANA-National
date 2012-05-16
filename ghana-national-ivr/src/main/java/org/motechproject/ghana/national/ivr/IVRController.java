package org.motechproject.ghana.national.ivr;

import org.motechproject.ghana.national.ivr.domain.CallSession;
import org.motechproject.ghana.national.ivr.domain.PatientOutbox;
import org.motechproject.ghana.national.ivr.repositories.AllCallSessions;
import org.motechproject.ghana.national.ivr.service.PatientAuthenticationService;
import org.motechproject.server.verboice.domain.VerboiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/ivr")
public class IVRController {

    @Autowired
    private AllCallSessions allCallSessions;

    @Autowired
    private PatientOutbox patientOutbox;

    @Autowired
    private PatientAuthenticationService patientAuthenticationService;

//    @Autowired
//    private VerboiceIVRController verboiceIVRController;


    @RequestMapping(value = "new_call", method = RequestMethod.POST)
    @ResponseBody
    public String newCall() {
        VerboiceResponse response = new VerboiceResponse();
        response.say("Press 1 to continue in English or press 2 to continue in Hindi");
        response.gather(getIVRUrl("receive_language_selection"), 1, '#', 10);
        return response.toXMLString();
    }

    @RequestMapping(value = "receive_language_selection", method = RequestMethod.POST)
    @ResponseBody
    public String receiveLanguageSelection(@RequestParam("Digits") String option, @RequestParam("CallSid") String sid) {
        VerboiceResponse response = new VerboiceResponse();
        if (option.equals("1") || option.equals("2")) {
            allCallSessions.add(new CallSession(sid).languageOption(option));
            response.say("Please enter your motech id");
            response.gather(getIVRUrl("authenticate_user"), 7, '#', 20);
        } else {
            response.redirect(getIVRUrl("receive_language_selection"));
        }
        return response.toXMLString();
    }

    @RequestMapping(value = "authenticate_user", method = RequestMethod.POST)
    @ResponseBody
    public String authenticateUser(@RequestParam("Digits") String motechId, @RequestParam("CallSid") String sid) {
        VerboiceResponse response = new VerboiceResponse();
        if (patientAuthenticationService.isRegistered(motechId)) {
            response.say("message for the patient from outbox");
        } else {
            response.say("Sorry the motech id that you entered is invalid");
            response.hangup();
        }
        return response.toXMLString();
    }


    private String getIVRUrl(String action) {
        return "http://10.4.3.111:8080/ghana-national-web/ivr/ " + action;
    }
}
