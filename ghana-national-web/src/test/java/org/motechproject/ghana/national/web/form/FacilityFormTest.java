package org.motechproject.ghana.national.web.form;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FacilityFormTest {

    private String id = " 123 ";
    private String name = " fname ";
    private String country = " country ";
    private String region = " region ";
    private String countyDistrict = " countyDistrict ";
    private String stateProvince = " stateProvince ";
    private String phoneNumber = " 0987654321 ";
    private String additionalPhoneNumber1 = " 0987654321 ";
    private String additionalPhoneNumber2 = " 0987654321 ";
    private String additionalPhoneNumber3 = " 0987654321 ";
    private String motechId = " 12345 ";

    @Test
    public void shouldTrimWhiteSpacesFromFields() {
        FacilityForm facilityForm = new FacilityForm(id, motechId, name, country, region, countyDistrict, stateProvince, phoneNumber, additionalPhoneNumber1, additionalPhoneNumber2, additionalPhoneNumber3);
        assertEquals("123",facilityForm.getId());
        assertEquals("fname",facilityForm.getName());
        assertEquals("country",facilityForm.getCountry());
        assertEquals("region",facilityForm.getRegion());
        assertEquals("countyDistrict",facilityForm.getCountyDistrict());
        assertEquals("0987654321",facilityForm.getPhoneNumber());
        assertEquals("0987654321",facilityForm.getAdditionalPhoneNumber1());
        assertEquals("0987654321",facilityForm.getAdditionalPhoneNumber2());
        assertEquals("0987654321",facilityForm.getAdditionalPhoneNumber3());
        assertEquals("12345",facilityForm.getMotechId());

    }
}
