package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.util.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AllMotechModuleOutPatientVisitsTest {

    JdbcTemplate mockJdbcTemplate;
    AllMotechModuleOutPatientVisits allMotechModuleOutPatientVisits;

    @Before
    public void setup() {
        mockJdbcTemplate = mock(JdbcTemplate.class);
        allMotechModuleOutPatientVisits = new AllMotechModuleOutPatientVisits();
        ReflectionTestUtils.setField(allMotechModuleOutPatientVisits, "jdbcTemplate", mockJdbcTemplate);
    }

    @Test
    public void shouldSaveOutPatientVisitEncounter() {
        OutPatientVisit outPatientVisit = new OutPatientVisit();
        String motechFacilityId = "11";
        String staffId = "12";
        PatientType registrantType = PatientType.OTHER;
        Date visitDate = DateUtil.now().toDate();
        Date birthDate = DateUtil.newDate(2000, 4, 4).toDate();
        Boolean isInsured = true;
        String nhis = "554";
        Date nhisExpiryDate = DateUtil.newDate(2015, 4, 4).toDate();
        Integer diagnosis = 1;
        String serialNumber = "serialNumber";
        Integer secondDiagnosis = 2;
        Boolean actTreated = true;
        Boolean rdtGiven = true;
        Boolean rdtPositive = false;
        Boolean isReferred = true;
        String comments = "q234trew";
        String gender = "F";
        outPatientVisit.setFacilityId(motechFacilityId)
                .setStaffId(staffId)
                .setRegistrantType(registrantType)
                .setVisitDate(visitDate)
                .setDateOfBirth(birthDate)
                .setInsured(isInsured)
                .setNhis(nhis)
                .setNewCase(true)
                .setNewPatient(true)
                .setSerialNumber(serialNumber)
                .setGender(gender)
                .setNhisExpires(nhisExpiryDate)
                .setDiagnosis(diagnosis)
                .setSecondDiagnosis(secondDiagnosis)
                .setActTreated(actTreated)
                .setRdtGiven(rdtGiven)
                .setRdtPositive(rdtPositive)
                .setReferred(isReferred)
                .setComments(comments);

        allMotechModuleOutPatientVisits.save(outPatientVisit);

        verify(mockJdbcTemplate).update("insert into motechmodule_generaloutpatientencounter (visit_date, staff_id, facility_id, serial_number," +
                " sex,birthdate,insured,newcase,newpatient,diagnosis,secondary_diagnosis,referred,rdt_given,rdt_positive," +
                "act_treated,comments) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", visitDate, staffId, motechFacilityId,
                serialNumber, "FEMALE", birthDate, 1, 1, 1, diagnosis, secondDiagnosis, 1, 1, 0, 1, comments);
    }

    @Test
    public void shouldSaveOutPatientVisitEncounterEvenIfGenderIsNull() {
        OutPatientVisit outPatientVisit = new OutPatientVisit();
        String motechFacilityId = "11";
        String staffId = "12";
        PatientType registrantType = PatientType.OTHER;
        Date visitDate = DateUtil.now().toDate();
        Date birthDate = DateUtil.newDate(2000, 4, 4).toDate();
        Boolean isInsured = true;
        String nhis = "554";
        Date nhisExpiryDate = DateUtil.newDate(2015, 4, 4).toDate();
        Integer diagnosis = 1;
        String serialNumber = "serialNumber";
        Integer secondDiagnosis = 2;
        Boolean actTreated = true;
        Boolean rdtGiven = true;
        Boolean rdtPositive = false;
        Boolean isReferred = true;
        String comments = "q234trew";
        outPatientVisit.setFacilityId(motechFacilityId)
                .setStaffId(staffId)
                .setRegistrantType(registrantType)
                .setVisitDate(visitDate)
                .setDateOfBirth(birthDate)
                .setInsured(isInsured)
                .setNhis(nhis)
                .setNewCase(true)
                .setNewPatient(true)
                .setSerialNumber(serialNumber)
                .setNhisExpires(nhisExpiryDate)
                .setDiagnosis(diagnosis)
                .setSecondDiagnosis(secondDiagnosis)
                .setActTreated(actTreated)
                .setRdtGiven(rdtGiven)
                .setRdtPositive(rdtPositive)
                .setReferred(isReferred)
                .setComments(comments);

        allMotechModuleOutPatientVisits.save(outPatientVisit);

        verify(mockJdbcTemplate).update("insert into motechmodule_generaloutpatientencounter (visit_date, staff_id, facility_id, serial_number," +
                " sex,birthdate,insured,newcase,newpatient,diagnosis,secondary_diagnosis,referred,rdt_given,rdt_positive," +
                "act_treated,comments) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", visitDate, staffId, motechFacilityId,
                serialNumber, null, birthDate, 1, 1, 1, diagnosis, secondDiagnosis, 1, 1, 0, 1, comments);
    }

    @Test
    public void shouldCheckForDuplicate() {
        String serialNumber = "serial";
        String facilityId = "facility";
        Date dateOfBirth = new Date();
        int diagnosis = 1;
        OutPatientVisit outPatientVisit = new OutPatientVisit();
        outPatientVisit.setSerialNumber(serialNumber);
        outPatientVisit.setFacilityId(facilityId);
        outPatientVisit.setDateOfBirth(dateOfBirth);
        outPatientVisit.setDiagnosis(diagnosis);
        allMotechModuleOutPatientVisits.isDuplicate(outPatientVisit);
        verify(mockJdbcTemplate).query(eq("SELECT id " +
                "FROM motechmodule_generaloutpatientencounter " +
                "WHERE serial_number = ? " +
                "AND facility_id = ? " +
                "AND birthdate = ? " +
                "AND diagnosis = ?"), eq(new Object[]{serialNumber, facilityId, dateOfBirth, diagnosis}), Matchers.<RowMapper<String>>any());
    }

    @Test
    public void shouldReturnTrueIfDuplicateIdFound() {
        String serialNumber = "serial";
        String facilityId = "facility";
        Date dateOfBirth = new Date();
        int diagnosis = 1;
        OutPatientVisit outPatientVisit = new OutPatientVisit();
        outPatientVisit.setSerialNumber(serialNumber);
        outPatientVisit.setFacilityId(facilityId);
        outPatientVisit.setDateOfBirth(dateOfBirth);
        outPatientVisit.setDiagnosis(diagnosis);
        when(mockJdbcTemplate.query(eq("SELECT id " +
                "FROM motechmodule_generaloutpatientencounter " +
                "WHERE serial_number = ? " +
                "AND facility_id = ? " +
                "AND birthdate = ? " +
                "AND diagnosis = ?"), eq(new Object[]{serialNumber, facilityId, dateOfBirth, diagnosis}), Matchers.<RowMapper<String>>any())).thenReturn(new ArrayList<String>() {{
            add("something");
        }});

        boolean duplicate = allMotechModuleOutPatientVisits.isDuplicate(outPatientVisit);
        assertTrue(duplicate);

    }
}
