package org.motechproject.ghana.national.repository;

import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class IVRGateway {

    @Autowired
    @Qualifier("VerboiceIVRService")
    private VerboiceIVRService verboiceIVRService;

    @Value("#{verboiceProperties['channel.name']}")
    private String channelName;

    public void placeCall(String phoneNumber, Map<String, String> params) {
        verboiceIVRService.initiateCall(new CallRequest(phoneNumber, params, channelName));
    }
}
