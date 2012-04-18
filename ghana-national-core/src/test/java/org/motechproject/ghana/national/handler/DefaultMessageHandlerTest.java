package org.motechproject.ghana.national.handler;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.SmsTemplateKeys;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.SMSGateway;

import java.util.HashSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DefaultMessageHandlerTest {

    @Mock
    AllFacilities mockAllFacilities;

    @Mock
    SMSGateway mockSmsGateway;

    @InjectMocks
    DefaultMessageHandler handler = new DefaultMessageHandler();

    @Test
    public void shouldSendDefaultMessageToAllFacilitiesOnTrigger() {
        initMocks(this);
        final String phone1 = "phone1";
        final String phone2 = "phone2";

        when(mockAllFacilities.getAllPhoneNumbers()).thenReturn(new HashSet<String>() {{
            add(phone1);
            add(phone2);
        }});
        handler.handleDefaultMessagesForFacility();

        verify(mockAllFacilities).getAllPhoneNumbers();
        verify(mockSmsGateway).dispatchSMSTextToAggregator(SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY,
                phone1, phone1);
        verify(mockSmsGateway).dispatchSMSTextToAggregator(SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY,
                phone2, phone2);
    }
}
