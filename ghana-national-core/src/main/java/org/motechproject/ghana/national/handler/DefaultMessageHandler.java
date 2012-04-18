package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.domain.SmsTemplateKeys;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

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
            smsGateway.dispatchSMSTextToAggregator(SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY,
                     facilityPhoneNumber, facilityPhoneNumber);
        }
    }
}
