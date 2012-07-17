package org.motechproject.ghana.national.domain.ivr;

import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;

public class ConnectToCallCenter {

    public Transition getAsTransition(String callCenterPhoneNumber) {
        return new Transition().setDestinationNode(getAsNode(callCenterPhoneNumber));
    }

    public Node getAsNode(String callCenterPhoneNumber){
        DialPrompt callCenterDialPrompt = new DialPrompt(callCenterPhoneNumber);
        callCenterDialPrompt.setCallerId(callCenterPhoneNumber);
        return new Node().addPrompts(callCenterDialPrompt);

    }
}
