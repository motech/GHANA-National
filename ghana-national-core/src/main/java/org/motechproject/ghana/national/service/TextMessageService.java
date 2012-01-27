package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Configuration;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllConfigurations;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextMessageService {

    @Autowired
    AllConfigurations allConfigurations;

    //@Autowired
    SmsService smsService;

    public void sendSMS(String recipient, Patient patient, String key) {
        MRSPatient mrsPatient = patient.getMrsPatient();
        MRSPerson person = mrsPatient.getPerson();
        Configuration configuration = allConfigurations.getConfigurationValue(key);
        String message = configuration.getValue();
        String messageWithTemplate = message.replace("${motechId}", mrsPatient.getMotechId()).replace("${firstName}",
                person.getFirstName()).replace("${lastName}", person.getLastName());
        smsService.sendSMS(recipient, messageWithTemplate);
    }
}
