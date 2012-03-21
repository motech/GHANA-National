package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.mrs.model.MRSFacility;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AllMotechModuleFacilitiesTest {

    AllMotechModuleFacilities allMotechModuleFacilities;
    JdbcTemplate mockJdbcTemplate;

    @Before
    public void setup() {
        mockJdbcTemplate = mock(JdbcTemplate.class);
        allMotechModuleFacilities = new AllMotechModuleFacilities();
        ReflectionTestUtils.setField(allMotechModuleFacilities, "jdbcTemplate", mockJdbcTemplate);
    }

    @Test
    public void shouldInsertARecordInMotechModuleFacility() {
        String locationId = "mrsId";
        String motechId = "motechId";
        String additionalPhoneNumber1 = "add1";
        String additionalPhoneNumber2 = "add2";
        String additionalPhoneNumber3 = "add3";
        String phoneNumber = "phone";

        Facility facility = new Facility(new MRSFacility(locationId, null, null, null, null, null));
        facility.motechId(motechId);
        facility.additionalPhoneNumber1(additionalPhoneNumber1);
        facility.additionalPhoneNumber2(additionalPhoneNumber2);
        facility.additionalPhoneNumber3(additionalPhoneNumber3);
        facility.mrsFacilityId(locationId);
        facility.phoneNumber(phoneNumber);
        allMotechModuleFacilities.save(facility);

        verify(mockJdbcTemplate).update("insert into motechmodule_facility " +
                "(facility_id,location_id,phone_number,additional_phone_number1,additional_phone_number2,additional_phone_number3) " +
                "values (?,?,?,?,?,?)", motechId, locationId, phoneNumber, additionalPhoneNumber1, additionalPhoneNumber2, additionalPhoneNumber3);
    }

    @Test
    public void shouldUpdateARecordInMotechModuleFacility() {
        String locationId = "mrsId";
        String motechId = "motechId";
        String additionalPhoneNumber1 = "add1";
        String additionalPhoneNumber2 = "add2";
        String additionalPhoneNumber3 = "add3";
        String phoneNumber = "phone";

        Facility facility = new Facility(new MRSFacility(locationId, null, null, null, null, null));
        facility.motechId(motechId);
        facility.additionalPhoneNumber1(additionalPhoneNumber1);
        facility.additionalPhoneNumber2(additionalPhoneNumber2);
        facility.additionalPhoneNumber3(additionalPhoneNumber3);
        facility.mrsFacilityId(locationId);
        facility.phoneNumber(phoneNumber);
        allMotechModuleFacilities.update(facility);

        verify(mockJdbcTemplate).update("update motechmodule_facility set " +
                "phone_number = ?,additional_phone_number1 = ?,additional_phone_number2 = ?,additional_phone_number3 = ? " +
                "where facility_id = ? ", phoneNumber, additionalPhoneNumber1, additionalPhoneNumber2, additionalPhoneNumber3, motechId);
    }
}
