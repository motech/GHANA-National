package org.motechproject.ghana.national.repository;

import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class IVRGateway {

    @Autowired
    @Qualifier("VerboiceIVRService")
    private VerboiceIVRService verboiceIVRService;

    public void placeCall(String phoneNumber, String language) {
        verboiceIVRService.initiateCall(new CallRequest(phoneNumber, 0, "demo_channel"));
    }
}
