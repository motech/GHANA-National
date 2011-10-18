package org.ghana.national.web.form;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mrs.services.Facility;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class CreateFacilityFormTest {

    private Facility setupFacility() {
        final String name = "facility";
        final String country = "country";
        final String region = "region";
        final String district = "district";
        final String province = "province";
        return new Facility(name, country, region, district, province);
    }

    private Facility facility = null;

    @Before
    public void setUp() {
        facility = this.setupFacility();
    }

    @Test
    public void testIfAFacilityIsDuplicateOrNot() {
        final CreateFacilityForm facilityForm = new CreateFacilityForm(facility.getName(), facility.getCountry(), facility.getRegion(), facility.getCountyDistrict(), facility.getStateProvince());
        assertTrue(facilityForm.isDuplicate(facility));
    }

    @Test
    public void testIfAFacilityIsNotDuplicateWhenNameIsUnique() {
        final CreateFacilityForm facilityForm = new CreateFacilityForm("new facility", facility.getCountry(), facility.getRegion(), facility.getCountyDistrict(), facility.getStateProvince());
        assertFalse(facilityForm.isDuplicate(facility));
    }

    @Test
    public void testIfAFacilityIsNotDuplicateWhenCountryIsUnique() {
        final CreateFacilityForm facilityForm = new CreateFacilityForm(facility.getName(), "new country", facility.getRegion(), facility.getCountyDistrict(), facility.getStateProvince());
        assertFalse(facilityForm.isDuplicate(facility));
    }

    @Test
    public void testIfAFacilityIsNotDuplicateWhenRegionIsUnique() {
        final CreateFacilityForm facilityForm = new CreateFacilityForm(facility.getName(), facility.getCountry(), "new region", facility.getCountyDistrict(), facility.getStateProvince());
        assertFalse(facilityForm.isDuplicate(facility));
    }

    @Test
    public void testIfAFacilityIsNotDuplicateWhenDistrictIsUnique() {
        final CreateFacilityForm facilityForm = new CreateFacilityForm(facility.getName(), facility.getCountry(), facility.getRegion(), "new district", facility.getStateProvince());
        assertFalse(facilityForm.isDuplicate(facility));
    }

    @Test
    public void testIfAFacilityIsNotDuplicateWheSubDistrictIsUnique() {
        final CreateFacilityForm facilityForm = new CreateFacilityForm(facility.getName(), facility.getCountry(), facility.getRegion(), facility.getCountyDistrict(), "new province");
        assertFalse(facilityForm.isDuplicate(facility));
    }

    @Test
    public void testIfAFacilityIsInAListOfFacilities() {
        final List<Facility> facilities = Arrays.asList(facility, new Facility("some facility"));
        CreateFacilityForm facilityFormSpy = spy(new CreateFacilityForm(facility.getName(), facility.getCountry(), facility.getRegion(), facility.getCountyDistrict(), facility.getStateProvince()));
        doReturn(true).when(facilityFormSpy).isDuplicate(facility);
        assertTrue(facilityFormSpy.isIn(facilities));

        assertFalse(facilityFormSpy.isIn(Arrays.asList(new Facility("facility one"), new Facility("facility two"))));
    }

}
