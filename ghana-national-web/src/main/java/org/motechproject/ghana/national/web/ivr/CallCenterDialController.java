package org.motechproject.ghana.national.web.ivr;


import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.domain.IVRCallCenterNoMapping;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.service.IvrCallCenterNoMappingService;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Calendar;
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
    @Value("#{ghanaNationalProperties['sip.channel.name']}")
    private String sipChannelName;
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

    @RequestMapping(value = "/dial/{ln}/callback/{callerPhoneNumber}", method = RequestMethod.POST)
    @ResponseBody
    public String handleCallStatus(@RequestParam("DialCallStatus") String dialCallStatus,
                                   @PathVariable("ln") String language,
                                   @PathVariable("callerPhoneNumber") String callerPhoneNumber,
                                   @RequestParam("nurseLine") boolean nurseLine) {
        if (dialCallStatus != null) {

            if (FAILED.getCode().equals(dialCallStatus)) {

                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_WEEK);
                if(day >= Calendar.MONDAY && day <= Calendar.SATURDAY) {

                    return playTwiml(Arrays.asList(ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_DIAL_FAILED.getFileName(), valueOf(language))));
                }
                else
                {
                    return playTwiml(Arrays.asList(ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_DIAL_FAILED.getFileName(), valueOf(language))));
                }

            } else if (BUSY.getCode().equals(dialCallStatus) || "no-answer".equals(dialCallStatus)) {
                return waitAndDial(language, callerPhoneNumber, nurseLine);
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

    private String waitAndDial(String language, String callerPhoneNumber, boolean nurseLine) {
        DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(DateUtil.today().dayOfWeek().get());
        IVRCallCenterNoMapping mapping = ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.valueOf(language), dayOfWeek, new Time(DateUtil.now().toLocalTime()), nurseLine);
        return (mapping != null) ? callCenterBusy(language, mapping, callerPhoneNumber, nurseLine) : callCenterClosed(language);
    }

    private String callCenterClosed(String language) {
        String url = ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_DIAL_FAILED.getFileName(), valueOf(language));
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml = xml + "<Response>\n";
        xml = xml + "    <Play>" + url + "</Play>\n";
        xml = xml + "</Response>";
        return xml;
    }

    private String callCenterBusy(String language, IVRCallCenterNoMapping mapping, String callerPhoneNumber, boolean nurseLine) {
        String url = ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_BUSY.getFileName(), valueOf(language));
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml = xml + "<Response>\n";
        xml = xml + "    <Play>" + url + "</Play>\n";
        xml = xml + "    <Pause length=\"" + callCenterRedialIntervalInSec + "\"/>";
        xml = xml + "    <Dial callerId=\"" + callerPhoneNumber + "\" action=\""
                + ivrCallbackUrlBuilder.callCenterDialStatusUrl(valueOf(language), callerPhoneNumber, nurseLine) + "\""
                + (mapping.isSipChannel() ? " channel=\"" + sipChannelName + "\">" : ">")
                + mapping.getPhoneNumber() + "</Dial>";
        xml = xml + "</Response>";
        return xml;
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
