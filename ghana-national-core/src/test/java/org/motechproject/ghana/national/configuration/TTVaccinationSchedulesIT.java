package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class TTVaccinationSchedulesIT extends BaseScheduleTrackingIT {
    private Time preferredAlertTime;

    @Before
    public void setUp() {
        super.setUp();
        preferredAlertTime = new Time(10, 10);
    }

    @Test
    @Ignore
    public void newPatientShouldBeGivenTheFirstTTDosageAndCreateAlertScheduleForSecondDosage() {
        LocalDate today = DateUtil.newDate(2000, 1, 1);
        mockCurrentDate(today);

        LocalDate firstDosageDate = today;

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("patient_id", CareScheduleNames.TTVaccine, preferredAlertTime, firstDosageDate);
        scheduleTrackingService.enroll(enrollmentRequest);

    }
}
