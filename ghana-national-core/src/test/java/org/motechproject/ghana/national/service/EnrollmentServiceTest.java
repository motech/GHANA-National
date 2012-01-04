package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Enrollment;
import org.motechproject.ghana.national.domain.MotechProgram;
import org.motechproject.ghana.national.repository.AllEnrollment;

import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnrollmentServiceTest {

    EnrollmentService enrollmentService;
    @Mock
    AllEnrollment allEnrollments;

    @Before
    public void before() {
        initMocks(this);
        enrollmentService = new EnrollmentService(allEnrollments);
    }

    @Test
    public void shouldFetchEnrollmentForPatient() {
        String patientId = "19090909";
        Enrollment enrollment = new Enrollment(patientId, new MotechProgram("CWC"));
        when(allEnrollments.findBy(patientId)).thenReturn(enrollment);
        assertSame(enrollment, enrollmentService.enrollmentFor(patientId));
    }
}
