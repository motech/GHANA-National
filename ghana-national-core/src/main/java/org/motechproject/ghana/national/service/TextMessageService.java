package org.motechproject.ghana.national.service;

import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.Configuration;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllConfigurations;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

@Service
public class TextMessageService {
    @Autowired
    AllConfigurations allConfigurations;
    @Autowired
    SmsService smsService;

    @Autowired
    private CMSLiteService cmsLiteService;

    public void sendLocalizedSMS(String recipient, Patient patient, String messageKey) throws ContentNotFoundException {
        MRSPatient mrsPatient = patient.getMrsPatient();
        StringContent cmsStringContent = cmsLiteService.getStringContent(Locale.getDefault().getLanguage(), messageKey);
        String smsToSend = fillUserNameAndMotechIdInfo(patient, mrsPatient.getPerson(), cmsStringContent.getValue());
        smsService.sendSMS(recipient, smsToSend);
    }

    public String sendSMS(String recipient, Patient patient, String template) {
        MRSPerson person = patient.getMrsPatient().getPerson();
        Configuration configuration = allConfigurations.getConfigurationValue(template);
        String message = configuration.getValue();
        String messageWithTemplate = fillUserNameAndMotechIdInfo(patient, person, message);
        smsService.sendSMS(recipient, messageWithTemplate);
        return messageWithTemplate;
    }

    private String fillUserNameAndMotechIdInfo(Patient patient, MRSPerson person, String message) {
        return message.replace(MOTECH_ID, patient.getMotechId()).replace(FIRST_NAME,
                person.getFirstName()).replace(LAST_NAME, person.getLastName());
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
