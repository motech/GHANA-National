package org.motechproject.ghana.national.web.ivr;


import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/ivr")
public class CallCenterDialController {

    IVRClipManager ivrClipManager;

    @Autowired
    public CallCenterDialController(IVRClipManager ivrClipManager,
                                    @Value("#{ghanaNationalProperties['callcenter.number']}")String phoneNumber,
                                    @Value("#{verboiceProperties['callcenter.redial.interval.sec']}") String redialIntervalInSec,
                                    IVRCallbackUrlBuilder ivrCallbackUrlBuilder) {
        this.ivrClipManager = ivrClipManager;
        this.callCenterPhoneNumber = phoneNumber;
        this.callCenterRedialIntervalInSec = redialIntervalInSec;
        callCenterDialStatusHandlerUrl = ivrCallbackUrlBuilder.callCenterDialStatusUrl();
    }

    private String callCenterPhoneNumber;
    private String callCenterRedialIntervalInSec;
    private String callCenterDialStatusHandlerUrl;

    @RequestMapping(value = "/dial/callback", method = RequestMethod.POST)
    @ResponseBody
    public String handleCallStatus(@RequestParam("DialCallStatus") String dialCallStatus, @RequestParam("ln") String language) {
        if (dialCallStatus != null) {
            if ("failed".equals(dialCallStatus)) {
                return playTwiml(Arrays.asList(ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_DIAL_FAILED.getFileName(), Language.valueOf(language))));
            } else if ("busy".equals(dialCallStatus) || "no-answer".equals(dialCallStatus)) {
                return waitAndDial(language);
            } else if ("completed".equals(dialCallStatus)) {
                return hangup();
            }
        }
        return hangup();
    }

    private String hangup() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<Response>\n" +
                "    <Hangup/>\n" +
                "</Response>";
    }

    private String waitAndDial(String language) {
        String url = ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_BUSY.getFileName(), Language.valueOf(language));
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml = xml + "    <Play>" + url + "</Play>\n";
        xml = xml + "    <Pause length=\"" + callCenterRedialIntervalInSec + "\"/>";
        xml = xml + "    <Dial callerId=\"" + callCenterPhoneNumber + "\" action=\"" + callCenterDialStatusHandlerUrl + "\">" + callCenterPhoneNumber + "</Dial>";
        return xml + "</Response>";
    }


    private String playTwiml(List<String> urls) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        for (String url : urls) {
            xml = xml + "    <Play>" + url + "</Play>\n";
        }
        return xml + "</Response>";
    }

}
