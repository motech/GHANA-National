package org.motechproject.ghana.national.service;

import org.motechproject.MotechException;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class TextMessageService {
    @Autowired
    SmsService smsService;

    @Autowired
    private CMSLiteService cmsLiteService;

    public String getSMSTemplate(String language, String key) {
        try {
            return cmsLiteService.getStringContent(language, key).getValue();
        } catch (ContentNotFoundException e) {
            throw new MotechException("Encountered error while retrieving SMS template", e);
        }
    }

    public String getSMSTemplate(String key) {
        return getSMSTemplate(defaultLanguage(), key);
    }

    public SMS getSMS(String templateKey, Map<String, String> placeholderValues) {
        return SMS.fromTemplate(getSMSTemplate(defaultLanguage(), templateKey)).fill(placeholderValues);
    }

    public SMS getSMS(String language, String templateKey, Map<String, String> placeholderValues) {
        return SMS.fromTemplate(getSMSTemplate(language, templateKey)).fill(placeholderValues);
    }

    public void sendSMS(Facility facility, SMS sms) {
        smsService.sendSMS(facility.phoneNumber(), sms.getText());
    }

    public void sendSMS(String phoneNumber, SMS sms) {
        smsService.sendSMS(phoneNumber, sms.getText());
    }

    private String defaultLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
