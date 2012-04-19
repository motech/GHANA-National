package org.motechproject.ghana.national.repository;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class AllMotechModuleFacilitiesIT extends BaseOpenMrsIntegrationTest {

    @Autowired
    private AllMotechModuleFacilities allMotechModuleFacilities;

    @Autowired
    private MRSFacilityAdapter mrsFacilityAdapter;

    @Test
    @Transactional(readOnly = true)
    public void shouldBeIdempotentOnDuplicateSaveOfFacility() {
        final MRSFacility mrsFacility = new MRSFacility("FacilityName", "Ghana", "Awutu", "Senya", "Province");
        String mrsFacilityId = mrsFacilityAdapter.saveFacility(mrsFacility).getId();

        Facility facility = new Facility(mrsFacility);
        final String motechId = "123456";
        facility.motechId(motechId).additionalPhoneNumber1("12345").additionalPhoneNumber2("0987654").additionalPhoneNumber3("098765432").mrsFacilityId(mrsFacilityId).phoneNumber("1234567890");
        assertFalse(allMotechModuleFacilities.facilityExists(motechId));
        allMotechModuleFacilities.save(facility);
        assertTrue(allMotechModuleFacilities.facilityExists(motechId));

        allMotechModuleFacilities.save(facility);
        assertThat(allMotechModuleFacilities.facility(motechId), is(asList(motechId)));
    }
}
