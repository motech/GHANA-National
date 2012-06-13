package org.motechproject.ghana.national.domain.mobilemidwife;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ServiceTypeTest {

    @Test
    public void shouldReturnServiceTypeEnum_GivenServiceNameAndMedium() {
        assertEquals(ServiceType.PREGNANCY_TEXT,ServiceType.getServiceType("Pregnancy", Medium.SMS));
        assertEquals(ServiceType.PREGNANCY_VOICE,ServiceType.getServiceType("Pregnancy", Medium.VOICE));
        assertEquals(ServiceType.CHILD_CARE_TEXT,ServiceType.getServiceType("Child Care", Medium.SMS));
        assertEquals(ServiceType.CHILD_CARE_VOICE,ServiceType.getServiceType("Child Care", Medium.VOICE));
    }
}
