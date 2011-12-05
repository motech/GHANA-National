package org.motechproject.ghana.national.web.form;

import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

public class FacilityFormTest {

    @Test
    public void shouldTrimExtraWhiteSpaces() {
        String id = "  123  ";
        String motechId = "  12345 ";
        String name = "  fname  ";
        String region = "  region  ";
        String country = "  country  ";
        String countyDistrict = "  countyDistrict  ";
        String stateProvince = "  stateProvince  ";
        String phoneNumber = "  0987654321  ";
        String additionalPhoneNumber1 = "  0987654321  ";
        String additionalPhoneNumber2 = "  0987654321  ";
        String additionalPhoneNumber3 = "  0987654321  ";

        FacilityForm facilityForm = new FacilityForm(id, motechId, name, country, region, countyDistrict, stateProvince, phoneNumber, additionalPhoneNumber1, additionalPhoneNumber2, additionalPhoneNumber3);
        assertEquals("123", facilityForm.getId());
        assertEquals("12345", facilityForm.getMotechId());
        assertEquals("fname", facilityForm.getName());
        assertEquals("region", facilityForm.getRegion());
        assertEquals("country", facilityForm.getCountry());
        assertEquals("countyDistrict", facilityForm.getCountyDistrict());
        assertEquals("stateProvince", facilityForm.getStateProvince());
        assertEquals("0987654321", facilityForm.getPhoneNumber());
        assertEquals("0987654321", facilityForm.getAdditionalPhoneNumber1());
        assertEquals("0987654321", facilityForm.getAdditionalPhoneNumber2());
        assertEquals("0987654321", facilityForm.getAdditionalPhoneNumber3());

    }
}
