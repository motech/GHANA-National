package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.IVRChannelMapping;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class IVRGateway {

    Logger logger = LoggerFactory.getLogger(IVRGateway.class);

    @Autowired
    private VerboiceIVRService verboiceIVRService;

    @Autowired
    private AllIvrChannelMappings allIvrChannelMappings;

    public void placeCall(String phoneNumber, Map<String, String> params) {
        String channelName = null;
        for (IVRChannelMapping ivrChannelMapping : allIvrChannelMappings.allMappings()) {
            Matcher channelNameMatcher = Pattern.compile(ivrChannelMapping.getPhoneNumberPattern()).matcher(phoneNumber);
            if (channelNameMatcher.find()) {
                channelName = ivrChannelMapping.getIvrChannel();
                break;
            }
        }
        if (channelName == null) {
            logger.warn("Unable to find a ivr channel for phone number: " + phoneNumber + ". Parameters for the call, " + params);
            channelName = allIvrChannelMappings.allMappings().get(0).getIvrChannel();
        }
        verboiceIVRService.initiateCall(new CallRequest(phoneNumber, params, channelName));
    }
}
