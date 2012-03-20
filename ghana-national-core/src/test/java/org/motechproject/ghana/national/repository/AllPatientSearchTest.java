package org.motechproject.ghana.national.repository;


import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class AllPatientSearchTest {

    private AllPatientSearch allPatientSearch =new AllPatientSearch();

    @Test
    public void testGetPatients() {
        String actualQuery = allPatientSearch.createQuery("FIRSTN", "LASTN", "9876543210", new Date(2012, 3, 15), null);
        String expectedQuery = "SELECT pname.given_name as firstName, pname.family_name as lastName, per.gender as gender, per.birthdate as dateOfBirth, p_identifier.identifier as motechId, loc.name as facility FROM person_name pname, patient pat, person per, patient_identifier p_identifier, location loc, person_attribute phone WHERE p_identifier.patient_id = pat.patient_id AND p_identifier.location_id = loc.location_id AND pat.voided = 0 AND p_identifier.identifier_type = 3 AND pat.patient_id=per.person_id AND per.person_id=pname.person_id AND pname.given_name LIKE 'FIRSTN%' AND pname.family_name LIKE 'LASTN%' AND per.birthdate = '3912-04-15' AND phone.person_id = per.person_id AND phone.person_attribute_type_id=8 AND phone.value='9876543210' limit 20";
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testGetPatientsUsingName() {
        String actualQuery = allPatientSearch.createQuery("FIRSTNAME", "LASTNAME", null, null, null);
        String expectedQuery = "SELECT pname.given_name as firstName, pname.family_name as lastName, per.gender as gender, per.birthdate as dateOfBirth, p_identifier.identifier as motechId, loc.name as facility FROM person_name pname, patient pat, person per, patient_identifier p_identifier, location loc WHERE p_identifier.patient_id = pat.patient_id AND p_identifier.location_id = loc.location_id AND pat.voided = 0 AND p_identifier.identifier_type = 3 AND pat.patient_id=per.person_id AND per.person_id=pname.person_id AND pname.given_name LIKE 'FIRSTNAME%' AND pname.family_name LIKE 'LASTNAME%' limit 20";
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testGetPatientsUsingDateOfBirthAlone() {
        String actualQuery = allPatientSearch.createQuery(null, null, null, new Date(2012, 3, 15), null);
        String expectedQuery = "SELECT pname.given_name as firstName, pname.family_name as lastName, per.gender as gender, per.birthdate as dateOfBirth, p_identifier.identifier as motechId, loc.name as facility FROM person_name pname, patient pat, person per, patient_identifier p_identifier, location loc WHERE p_identifier.patient_id = pat.patient_id AND p_identifier.location_id = loc.location_id AND pat.voided = 0 AND p_identifier.identifier_type = 3 AND pat.patient_id=per.person_id AND per.person_id=pname.person_id AND per.birthdate = '3912-04-15' limit 20";
        assertEquals(expectedQuery, actualQuery);
    }


}
