package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Properties;

@Component
public class SMSTextContentSeed extends Seed {

    @Autowired
    private CMSLiteService cmsLiteService;
    @Autowired
    @Qualifier("textMessageProperties")
    private Properties textMessages;

    @Override
    protected void load() {
        try {
            for (String property : textMessages.stringPropertyNames()) {
                cmsLiteService.addContent(new StringContent(Locale.ENGLISH.getLanguage(), property, textMessages.getProperty(property)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
