package org.motechproject.ghana.national.domain.ivr;

import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;

public class ConnectToCallCenter {

    public Transition getAsTransition(String callCenterPhoneNumber, String dialStatusHandlerUrl) {
        return new Transition().setDestinationNode(getAsNode(callCenterPhoneNumber, dialStatusHandlerUrl));
    }

    public Node getAsNode(String callCenterPhoneNumber, String dialStatusHandlerUrl){
        DialPrompt callCenterDialPrompt = new DialPrompt(callCenterPhoneNumber);
        callCenterDialPrompt.setCallerId(callCenterPhoneNumber);
        callCenterDialPrompt.setAction(dialStatusHandlerUrl);
        return new Node().addPrompts(callCenterDialPrompt);
    }
}
