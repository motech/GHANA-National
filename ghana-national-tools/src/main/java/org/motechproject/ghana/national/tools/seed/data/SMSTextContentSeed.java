package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.configuration.TextMessageTemplates;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SMSTextContentSeed extends Seed {

    @Autowired
    private CMSLiteService cmsLiteService;

    @Override
    protected void load() {
        try {
            cmsLiteService.addContent(new StringContent(Locale.ENGLISH.getLanguage(), TextMessageTemplates.REGISTER_SUCCESS_SMS[0], TextMessageTemplates.REGISTER_SUCCESS_SMS[1]));
            cmsLiteService.addContent(new StringContent(Locale.ENGLISH.getLanguage(), TextMessageTemplates.DELIVERY_NOTIFICATION_SMS[0], TextMessageTemplates.DELIVERY_NOTIFICATION_SMS[1]));
            cmsLiteService.addContent(new StringContent(Locale.ENGLISH.getLanguage(), TextMessageTemplates.PREGNANCY_ALERT_SMS[0], TextMessageTemplates.PREGNANCY_ALERT_SMS[1]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
