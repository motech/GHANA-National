package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

import static ch.lambdaj.Lambda.join;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.WINDOW_NAMES;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY;

@Component
public class DefaultMessageHandler {

    @Autowired
    AllFacilities allFacilities;

    @Autowired
    SMSGateway smsGateway;

    @LoginAsAdmin
    @ApiSession
    public void handleDefaultMessagesForFacility() {
        Set<String> facilityPhoneNumbers = allFacilities.getAllPhoneNumbers();
        for (String facilityPhoneNumber : facilityPhoneNumbers) {
            smsGateway.dispatchSMSToAggregator(FACILITIES_DEFAULT_MESSAGE_KEY, new HashMap<String, String>(){{
                put(WINDOW_NAMES, join(AlertWindow.ghanaNationalWindowNames(),", "));
            }}, facilityPhoneNumber, facilityPhoneNumber);
        }
    }
}
