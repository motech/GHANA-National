package org.motechproject.ghana.national.repository;

import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.Enrollment;
import org.motechproject.ghana.national.domain.MotechProgram;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AllEnrollmentTestIT extends BaseIntegrationTest {

    @Autowired
    private AllEnrollment allEnrollment;

    @Test
    public void shouldFindEnrollmentByPatientId() {
        String patientId = "123456";
        MotechProgram motechProgram = new MotechProgram("CWC");
        Enrollment enrollment = new Enrollment(patientId, motechProgram);
        allEnrollment.add(enrollment);
        
        Enrollment actual = allEnrollment.findBy(patientId);
        assertThat(actual.getPatientId(),is(equalTo(patientId)));
        assertThat(actual.getProgram().getName(),is(equalTo(motechProgram.getName())));
    }

    @Test
    public void shouldReturnNullIfPatientIdIsNotFound() {
        assertNull(allEnrollment.findBy("1234567"));
    }

}
