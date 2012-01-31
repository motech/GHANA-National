package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.domain.Configuration;
import org.motechproject.ghana.national.domain.Constants;
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

    @Value("#{message['delivery_notification_sms']}")
    String deliveryNotificationSms;

    @Override
    protected void load() {
        try {
            allConfigurations.add(new Configuration(Constants.REGISTER_SUCCESS_SMS, registrationSms));
            allConfigurations.add(new Configuration(Constants.DELIVERY_NOTIFICATION_SMS, deliveryNotificationSms));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
