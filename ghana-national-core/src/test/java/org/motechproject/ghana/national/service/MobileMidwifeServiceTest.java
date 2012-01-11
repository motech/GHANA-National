package org.motechproject.ghana.national.service;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.model.DayOfWeek;

import static org.mockito.MockitoAnnotations.initMocks;

public class MobileMidwifeServiceTest {
    @Mock
    private AllMobileMidwifeEnrollments enrollments;
    private MobileMidwifeService service;

    public MobileMidwifeServiceTest() {
        initMocks(this);
        service = new MobileMidwifeService();
    }

    @Test
    @Ignore
    public void shouldSaveMobileMidwifeEnrollment() {
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId("patientId").staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday)
                .build();
        service.save(enrollment);
        service.findBy(enrollment.getId());
    }

}
