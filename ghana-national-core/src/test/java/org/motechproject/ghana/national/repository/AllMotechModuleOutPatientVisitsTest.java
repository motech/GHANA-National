package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.util.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.mockito.Mockito.mock;

public class AllMotechModuleOutPatientVisitsTest {

    JdbcTemplate mockJdbcTemplate;
    AllMotechModuleOutPatientVisits allMotechModuleOutPatientVisits;

    @Before
    public void setup() {
        mockJdbcTemplate = mock(JdbcTemplate.class);
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
                .setNhisExpires(nhisExpiryDate)
                .setDiagnosis(diagnosis)
                .setSecondDiagnosis(secondDiagnosis)
                .setActTreated(actTreated)
                .setRdtGiven(rdtGiven)
                .setRdtPositive(rdtPositive)
                .setReferred(isReferred)
                .setComments(comments);

    }
}
