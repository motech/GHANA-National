package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.configuration.TextMessageTemplateVariables;
import org.motechproject.ghana.national.messagegateway.domain.MessageRecipientType;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class DefaultMessageFeederTest {

    @Mock
    AllFacilities mockAllFacilities;

    @Mock
    SMSGateway mockSmsGateway;

    @InjectMocks
    DefaultMessageFeeder messageFeeder = new DefaultMessageFeeder();

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldSendDefaultTextMessageToAllFacilitiesWithPhoneNumberAndFacilityName() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        String phoneNumber1 = "1234";
        String phoneNumber2 = "1233";
        final String facility1 = "facility1";
        hashMap.put(phoneNumber1, facility1);
        final String facility2 = "facility2";
        hashMap.put(phoneNumber2, facility2);
        when(mockAllFacilities.getAllFacilityNameToMotechFacilityIdMapping()).thenReturn(hashMap);

        messageFeeder.handleDefaultMessagesForFacility();

        ArgumentCaptor<Map> templateValuesCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> phoneNumberCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> identifierCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSmsGateway, times(2)).dispatchSMSToAggregator(eq(FACILITIES_DEFAULT_MESSAGE_KEY), templateValuesCaptor.capture(), phoneNumberCaptor.capture(), identifierCaptor.capture(), eq(MessageRecipientType.FACILITY));
        List<Map> allTemplateValues = templateValuesCaptor.getAllValues();
        phoneNumberCaptor.getAllValues();
        assertReflectionEquals(asList(
                new HashMap<String, String>() {{
                    put(TextMessageTemplateVariables.FACILITY, facility1);
                    put(TextMessageTemplateVariables.WINDOW_NAMES, "Upcoming, Due, Overdue");
                }},
                new HashMap<String, String>() {{
            put(TextMessageTemplateVariables.FACILITY, facility2);
            put(TextMessageTemplateVariables.WINDOW_NAMES, "Upcoming, Due, Overdue");
        }}
        ), templateValuesCaptor.getAllValues(), ReflectionComparatorMode.LENIENT_ORDER);

        assertReflectionEquals(asList(phoneNumber1, phoneNumber2), identifierCaptor.getAllValues(),ReflectionComparatorMode.LENIENT_ORDER);
        assertReflectionEquals(asList(phoneNumber1, phoneNumber2), phoneNumberCaptor.getAllValues(),ReflectionComparatorMode.LENIENT_ORDER);

    }
}

