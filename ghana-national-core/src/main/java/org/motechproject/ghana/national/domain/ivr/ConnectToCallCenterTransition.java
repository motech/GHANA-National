package org.motechproject.ghana.national.domain.ivr;

import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;

public class ConnectToCallCenterTransition {

    public Transition get(String callCenterPhoneNumber) {
        DialPrompt callCenterDialPrompt = new DialPrompt(callCenterPhoneNumber);
        callCenterDialPrompt.setCallerId(callCenterPhoneNumber);
        return new Transition().setDestinationNode(new Node().addPrompts(callCenterDialPrompt));
    }


}
