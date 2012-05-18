package org.motechproject.ghana.national.handler;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.SmsTemplateKeys;
import org.motechproject.ghana.national.messagegateway.domain.MessageRecipientType;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.SMSGateway;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.FACILITY;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.WINDOW_NAMES;

public class DefaultMessageHandlerTest {

    @Mock
    AllFacilities mockAllFacilities;

    @Mock
    SMSGateway mockSmsGateway;

    @InjectMocks
    DefaultMessageFeeder handler = new DefaultMessageFeeder();

    @Test
    public void shouldSendDefaultMessageToAllFacilitiesOnTrigger() {
        initMocks(this);
        final String phone1 = "phone1";
        final String phone2 = "phone2";

        final String facility1 = "facility1";
        final String facility2 = "facility2";
        when(mockAllFacilities.getAllFacilityNameToMotechFacilityIdMapping()).thenReturn(new HashMap<String, String>() {{
            put(phone1, facility1);
            put(phone2, facility2);
        }});
        handler.handleDefaultMessagesForFacility();

        verify(mockAllFacilities).getAllFacilityNameToMotechFacilityIdMapping();
        verify(mockSmsGateway).dispatchSMSToAggregator(SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY,
                new HashMap<String, String>() {{
                    put(WINDOW_NAMES, "Upcoming, Due, Overdue");
                    put(FACILITY, facility1);
                }}, phone1, phone1, MessageRecipientType.FACILITY);
        verify(mockSmsGateway).dispatchSMSToAggregator(SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY,
                new HashMap<String, String>() {{
                    put(WINDOW_NAMES, "Upcoming, Due, Overdue");
                    put(FACILITY, facility2);
                }}, phone2, phone2, MessageRecipientType.FACILITY);
    }
}
