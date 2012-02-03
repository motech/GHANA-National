package org.motechproject.ghana.national.service;

import org.apache.commons.io.FileUtils;
import org.motechproject.ghana.national.domain.Configuration;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllConfigurations;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

@Service
public class TextMessageService {
    @Autowired
    AllConfigurations allConfigurations;
    @Autowired
    SmsService smsService;

    public String sendSMS(String recipient, Patient patient, String template) {
        MRSPerson person = patient.getMrsPatient().getPerson();
        Configuration configuration = allConfigurations.getConfigurationValue(template);
        String message = configuration.getValue();
        String messageWithTemplate = message.replace(MOTECH_ID, patient.getMotechId()).replace(FIRST_NAME,
                                                    person.getFirstName()).replace(LAST_NAME, person.getLastName());
        smsService.sendSMS(recipient, messageWithTemplate);
        return messageWithTemplate;
    }

    public void sendSMS(Facility facility, String message) {
//        try {
//            FileUtils.writeStringToFile(new File("/temp/sms.txt"), String.format("PhoneNumber:%s, %s", facility.getPhoneNumber(), message));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        smsService.sendSMS(facility.getPhoneNumber(), message);
    }
}
