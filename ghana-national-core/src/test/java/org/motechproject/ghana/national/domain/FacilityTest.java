package org.motechproject.ghana.national.domain;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FacilityTest {

    @Test
    public void shouldReturnAllFacilityPhoneNumbers() {
        String phoneNumber = "phoneNumber";
        String phoneNumber1 = "phoneNumber1";
        Facility facility=new Facility().phoneNumber(phoneNumber).additionalPhoneNumber1(phoneNumber1);
        assertThat(facility.findAllPhoneNumbers(), is(Arrays.asList(phoneNumber, phoneNumber1)));

    }
}
