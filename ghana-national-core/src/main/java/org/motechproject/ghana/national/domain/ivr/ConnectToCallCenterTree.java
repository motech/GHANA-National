package org.motechproject.ghana.national.domain.ivr;

import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.service.IvrCallCenterNoMappingService;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectToCallCenterTree {

    @Autowired
    private IVRCallbackUrlBuilder ivrCallbackUrlBuilder;

    @Autowired
    private IvrCallCenterNoMappingService ivrCallCenterNoMappingService;

    public Transition getAsTransition(Language language) {
        return new Transition().setDestinationNode(getAsNode(language));
    }

    public Node getAsNode(Language language){
        DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(DateUtil.today().dayOfWeek().get());
        String callCenterPhoneNumber = ivrCallCenterNoMappingService.getCallCenterPhoneNumber(language, dayOfWeek, new Time(DateUtil.now().toLocalTime()));
        DialPrompt callCenterDialPrompt = new DialPrompt(callCenterPhoneNumber);
        callCenterDialPrompt.setCallerId(callCenterPhoneNumber);
        callCenterDialPrompt.setAction(ivrCallbackUrlBuilder.callCenterDialStatusUrl(language));
        return new Node().addPrompts(callCenterDialPrompt);
    }
}
