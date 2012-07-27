package org.motechproject.ghana.national.web.ivr;


import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.domain.IVRCallCenterNoMapping;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.VerboiceDialStatus;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.service.IvrCallCenterNoMappingService;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.motechproject.ghana.national.domain.ivr.VerboiceDialStatus.BUSY;
import static org.motechproject.ghana.national.domain.ivr.VerboiceDialStatus.COMPLETED;
import static org.motechproject.ghana.national.domain.ivr.VerboiceDialStatus.FAILED;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.valueOf;

@Controller
@RequestMapping(value = "/ivr")
public class CallCenterDialController {

    private IVRClipManager ivrClipManager;
    private String callCenterRedialIntervalInSec;
    private IVRCallbackUrlBuilder ivrCallbackUrlBuilder;
    private IvrCallCenterNoMappingService ivrCallCenterNoMappingService;

    @Autowired
    public CallCenterDialController(IVRClipManager ivrClipManager,
                                    @Value("#{verboiceProperties['callcenter.redial.interval.sec']}") String redialIntervalInSec,
                                    IVRCallbackUrlBuilder ivrCallbackUrlBuilder,
                                    IvrCallCenterNoMappingService ivrCallCenterNoMappingService) {
        this.ivrClipManager = ivrClipManager;
        this.callCenterRedialIntervalInSec = redialIntervalInSec;
        this.ivrCallbackUrlBuilder = ivrCallbackUrlBuilder;
        this.ivrCallCenterNoMappingService = ivrCallCenterNoMappingService;
    }

    @RequestMapping(value = "/dial/{ln}/callback", method = RequestMethod.POST)
    @ResponseBody
    public String handleCallStatus(@RequestParam("DialCallStatus") String dialCallStatus, @PathVariable("ln") String language) {
        if (dialCallStatus != null) {
            if (FAILED.getCode().equals(dialCallStatus)) {
                return playTwiml(Arrays.asList(ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_DIAL_FAILED.getFileName(), valueOf(language))));
            } else if (BUSY.getCode().equals(dialCallStatus) || "no-answer".equals(dialCallStatus)) {
                return waitAndDial(language);
            } else if (COMPLETED.getCode().equals(dialCallStatus)) {
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
        DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(DateUtil.today().dayOfWeek().get());
        String callCenterPhoneNumber = ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.valueOf(language), dayOfWeek, new Time(DateUtil.now().toLocalTime()));

        String url = ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_BUSY.getFileName(), valueOf(language));
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml = xml + "<Response>\n";
        xml = xml + "    <Play>" + url + "</Play>\n";
        xml = xml + "    <Pause length=\"" + callCenterRedialIntervalInSec + "\"/>";
        xml = xml + "    <Dial callerId=\"" + callCenterPhoneNumber + "\" action=\"" + ivrCallbackUrlBuilder.callCenterDialStatusUrl(valueOf(language)) + "\">" + callCenterPhoneNumber + "</Dial>";
        return xml + "</Response>";
    }


    private String playTwiml(List<String> urls) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml = xml + "<Response>\n";
        for (String url : urls) {
            xml = xml + "    <Play>" + url + "</Play>\n";
        }
        return xml + "</Response>";
    }

}
