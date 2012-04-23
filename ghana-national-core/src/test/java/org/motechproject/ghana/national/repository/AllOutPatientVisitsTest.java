package org.motechproject.ghana.national.repository;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.domain.PatientType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.today;

public class AllOutPatientVisitsTest extends BaseIntegrationTest {
    @Autowired
    AllOutPatientVisits allOutPatientVisits;
    @Mock
    AllMotechModuleOutPatientVisits mockAllMotechModuleOutPatientVisits;

    @Before
    public void init() {
        initMocks(this);
        ReflectionTestUtils.setField(allOutPatientVisits, "allMotechModuleOutPatientVisits",
                mockAllMotechModuleOutPatientVisits);
    }


    @Test
    public void shouldAddANewOutPatientVisit() {
        OutPatientVisit visit = new OutPatientVisit();
        Date dateOfBirth = new Date(2010, 10, 10);
        visit.setDateOfBirth(dateOfBirth);

        allOutPatientVisits.add(visit);

        List<OutPatientVisit> all = allOutPatientVisits.getAll();
        assertEquals(1, all.size());
        assertEquals(dateOfBirth, all.get(0).getDateOfBirth());
        verify(mockAllMotechModuleOutPatientVisits).save(visit);
    }

    @Test
    public void shouldBeIdempotentOnDuplicateOutpatientCreation() {
        allOutPatientVisits.add(outpatientVisit());
        assertThat(allOutPatientVisits.findBy("fac A", "staffid1", "1234", newDate(2012, 3, 3).toDate()).size(), is(1));

        allOutPatientVisits.add(outpatientVisit());
        assertThat(allOutPatientVisits.findBy("fac A", "staffid1", "1234", newDate(2012, 3, 3).toDate()).size(), is(1));

        final OutPatientVisit opvNhis = outpatientVisit().setNhis("new Nhis");
        allOutPatientVisits.add(opvNhis);
        assertThat(allOutPatientVisits.findBy("fac A", "staffid1", "1234", newDate(2012, 3, 3).toDate()).size(), is(2));
    }

    private OutPatientVisit outpatientVisit() {
        return new OutPatientVisit().setActTreated(true).setComments("comments").setDateOfBirth(newDateTime(2012, 2, 2, 1, 1, 1).toDate())
                .setDiagnosis(1).setGender("M").setFacilityId("fac A")
                .setInsured(true).setNewCase(true).setNewPatient(true).setNhis("Insurance").setNhisExpires(today().plusYears(3).toDate()).setRdtGiven(true).setRdtPositive(false).setReferred(true)
                .setRegistrantType(PatientType.OTHER).setSecondDiagnosis(2).setStaffId("staffid1").setSerialNumber("1234").setVisitDate(newDate(2012, 3, 3).toDate());
    }

    @Test
    public void shouldFindOutpatientsByFacility_Staff_SerialNumberAndDateOfBirth() {

        allOutPatientVisits.add(new OutPatientVisit().setStaffId("staffid1").setFacilityId("fac A")
                .setSerialNumber("1234").setVisitDate(newDate(2012, 3, 3).toDate()));
        allOutPatientVisits.add(new OutPatientVisit().setStaffId("staffid2").setFacilityId("fac A")
                .setSerialNumber("1234").setVisitDate(newDate(2012, 3, 3).toDate()));
        allOutPatientVisits.add(new OutPatientVisit().setStaffId("staffid3").setFacilityId("fac A")
                .setSerialNumber("12345").setVisitDate(newDate(2012, 3, 5).toDate()));
        allOutPatientVisits.add(new OutPatientVisit().setStaffId("staffid3").setFacilityId("fac A")
                .setSerialNumber("12345").setVisitDate(newDate(2012, 3, 5).toDate()).setSecondDiagnosis(2));

        assertThat(allOutPatientVisits.findBy("fac A", "staffid1", "1234", newDate(2012, 3, 3).toDate()).size(), CoreMatchers.is(1));
        assertThat(allOutPatientVisits.findBy("fac A", "staffid3", "12345", newDate(2012, 3, 5).toDate()).size(), CoreMatchers.is(2));
    }

    @After
    public void tearDown() {
       allOutPatientVisits.removeAll();
    }
}
