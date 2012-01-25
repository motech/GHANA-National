package org.motechproject.ghana.national.service;

import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.model.DayOfWeek;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MobileMidwifeServiceTest {
    @Mock
    private AllMobileMidwifeEnrollments allEnrollments;
    private MobileMidwifeService service;

    public MobileMidwifeServiceTest() {
        initMocks(this);
        service = new MobileMidwifeService();
        ReflectionTestUtils.setField(service, "allEnrollments", allEnrollments);
    }

    @Test
    public void shouldCreateMobileMidwifeEnrollmentIfNotExists() {
        String patientId = "patientId";
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday)
                .build();
        when(allEnrollments.findByPatientId(patientId)).thenReturn(null);

        service.register(enrollment);
        verify(allEnrollments).add(enrollment);
    }
    
    @Test
    public void shouldRemoveExistingAndCreateMobileMidwifeEnrollmentIfNotExists() {
        String patientId = "patientId";
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday)
                .build();
        MobileMidwifeEnrollment existingEnrollment = mock(MobileMidwifeEnrollment.class);
        when(allEnrollments.findByPatientId(patientId)).thenReturn(existingEnrollment);

        service.register(enrollment);
        assertTrue(enrollment.getActive());
        verify(existingEnrollment).setActive(eq(false));
        verify(allEnrollments).update(existingEnrollment);
        verify(allEnrollments).add(enrollment);
    }

    @Test
    public void shouldFindMobileMidwifeEnrollmentByPatientId() {

        String patientId = "patientId";
        service.findBy(patientId);
        verify(allEnrollments).findByPatientId(patientId);
    }

}
