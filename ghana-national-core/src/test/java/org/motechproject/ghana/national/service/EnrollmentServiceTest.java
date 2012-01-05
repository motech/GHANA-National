package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.CWCEnrollment;
import org.motechproject.ghana.national.domain.MotechProgram;
import org.motechproject.ghana.national.repository.AllEnrollment;
import org.motechproject.ghana.national.vo.MotechProgramName;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnrollmentServiceTest {

    EnrollmentService enrollmentService;
    @Mock
    AllEnrollment mockAllEnrollments;

    @Before
    public void before() {
        initMocks(this);
        enrollmentService = new EnrollmentService(mockAllEnrollments);
    }

    @Test
    public void shouldFetchEnrollmentForPatient() {
        String patientId = "19090909";
        CWCEnrollment CWCEnrollment = new CWCEnrollment(patientId, new MotechProgram(MotechProgramName.CWC));
        when(mockAllEnrollments.findBy(patientId)).thenReturn(CWCEnrollment);
        assertSame(CWCEnrollment, enrollmentService.cwcEnrollmentFor(patientId));
    }

    @Test
    public void shouldSaveEnrollmentIfItDoesNotExist() {
        String patientId = "newPatient";
        CWCEnrollment CWCEnrollment = new CWCEnrollment(patientId, new MotechProgram(MotechProgramName.CWC));
        enrollmentService.saveOrUpdate(CWCEnrollment);
        verify(mockAllEnrollments).add(CWCEnrollment);
    }

    @Test
    public void shouldUpdateEnrollmentIfItExist() {
        String patientId = "newPatient";
        String serialNumber = "serialNumber";
        String facilityId = "facilityId";
        Date registrationDate = DateUtil.now().toDate();

        CWCEnrollment CWCEnrollment = new CWCEnrollment(patientId, new MotechProgram(MotechProgramName.CWC));
        CWCEnrollment.facilityId(facilityId);
        CWCEnrollment.registrationDate(registrationDate);
        CWCEnrollment.serialNumber(serialNumber);

        MotechProgramName programName = MotechProgramName.CWC;
        CWCEnrollment existingCWCEnrollment = new CWCEnrollment(patientId, new MotechProgram(programName));

        when(mockAllEnrollments.findBy(patientId, programName)).thenReturn(existingCWCEnrollment);

        enrollmentService.saveOrUpdate(CWCEnrollment);

        verify(mockAllEnrollments).update(existingCWCEnrollment);
        assertEnrollment(facilityId, patientId, programName, registrationDate, existingCWCEnrollment);
    }

    private void assertEnrollment(String facilityId, String patientId, MotechProgramName motechProgram, Date registrationDate, CWCEnrollment CWCEnrollment) {
        assertThat(CWCEnrollment.getFacilityId(), is(equalTo(facilityId)));
        assertThat(CWCEnrollment.getPatientId(), is(equalTo(patientId)));
        assertThat(CWCEnrollment.getProgram().getName(), is(equalTo(motechProgram)));
        assertThat(CWCEnrollment.getRegistrationDate(), is(equalTo(registrationDate)));
    }

}
