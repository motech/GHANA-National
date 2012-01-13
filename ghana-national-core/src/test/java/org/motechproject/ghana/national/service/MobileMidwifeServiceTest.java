package org.motechproject.ghana.national.service;

import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.model.DayOfWeek;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MobileMidwifeServiceTest {
    @Mock
    private AllMobileMidwifeEnrollments enrollments;
    private MobileMidwifeService service;

    public MobileMidwifeServiceTest() {
        initMocks(this);
        service = new MobileMidwifeService();
        ReflectionTestUtils.setField(service, "allEnrollments", enrollments);
    }

    @Test
    public void shouldCreateOrUpdateMobileMidwifeEnrollment() {
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId("patientId").staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday)
                .build();
        service.saveOrUpdate(enrollment);
        verify(enrollments).createOrUpdate(enrollment);
    }
    
    @Test
    public void shouldFindMobileMidwifeEnrollmentByPatientId() {

        String patientId = "patientId";
        service.findBy(patientId);
        verify(enrollments).findByPatientId(patientId);
    }

}
