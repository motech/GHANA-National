package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.domain.Configuration;
import org.motechproject.ghana.national.repository.AllConfigurations;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SMSTextContentSeed extends Seed {

    @Autowired
    AllConfigurations allConfigurations;

    @Value("#{message['registration_sms']}")
    String registrationSms;

    @Override
    protected void load() {
        try {
            Configuration configuration = new Configuration("REGISTER_SUCCESS_SMS", registrationSms);
            allConfigurations.add(configuration);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
