package org.motechproject.ghana.national.domain.ivr.transition;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
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
import org.springframework.stereotype.Component;

@Component
public class ConnectToCallCenterTransition extends Transition {

    @Autowired
    private IVRCallbackUrlBuilder ivrCallbackUrlBuilder;

    @Autowired
    private IVRClipManager ivrClipManager;

    @Autowired
    private IvrCallCenterNoMappingService ivrCallCenterNoMappingService;

    @JsonProperty
    private Language language;

    @JsonProperty
    private boolean nurseLine;

    @Value("#{ghanaNationalProperties['sip.channel.name']}")
    private String sipChannelName;

    // Required for Ektorp
    public ConnectToCallCenterTransition() {
    }

    public ConnectToCallCenterTransition(Language language) {
        this.language = language;
    }

    public ConnectToCallCenterTransition(boolean nurseLine) {
        this.nurseLine = nurseLine;
        this.language = Language.EN;
    }

    @Override
    public Node getDestinationNode(String input, FlowSession session) {
        String callerId = session.get("From");
        return getAsNode(language, callerId);
    }

    public Node getAsNode(Language language, String callerPhoneNumber) {
        DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(DateUtil.today().dayOfWeek().get());
        IVRCallCenterNoMapping mapping = ivrCallCenterNoMappingService.getCallCenterPhoneNumber(language, dayOfWeek, new Time(DateUtil.now().toLocalTime()), nurseLine);

        if (mapping == null) {
            return new Node().addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(AudioPrompts.CALL_CENTER_DIAL_FAILED.value(), Language.EN)));
        }

        DialPrompt callCenterDialPrompt = new DialPrompt(mapping.getPhoneNumber());
        callCenterDialPrompt.setCallerId(callerPhoneNumber);
        if (mapping.isSipChannel()) {
            callCenterDialPrompt.setChannel(sipChannelName);
        }
        callCenterDialPrompt.setAction(ivrCallbackUrlBuilder.callCenterDialStatusUrl(language, callerPhoneNumber, nurseLine));
        return new Node().addPrompts(callCenterDialPrompt);
    }

    public ConnectToCallCenterTransition setNurseLine(boolean nurseLine) {
        this.nurseLine = nurseLine;
        return this;
    }
}
