package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class EDDAlertSchedulesTest extends BaseScheduleTrackingTest {
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void verifyPregnancyScheduleWhenEDDIsVeryFarInFuture() throws SchedulerException {
        mockToday(newDate(2012, 3, 1));
        LocalDate expectedDeliveryDate = newDate(2012, 11, 10);

        enrollmentId = scheduleAlertForDeliveryNotfication(expectedDeliveryDate);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2012, 11, 10), newDate(2012, 11, 17), newDate(2012, 11, 24), newDate(2012, 12, 1)));
    }

    @Test
    public void verifyPregnancyScheduleWhenEDDIsUnderOneWeekFromToday() throws SchedulerException {
        mockToday(newDate(2012, 2, 1));
        LocalDate expectedDeliveryDate = newDate(2012, 2, 3);

        enrollmentId = scheduleAlertForDeliveryNotfication(expectedDeliveryDate);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2012, 2, 3), newDate(2012, 2, 10), newDate(2012, 2, 17), newDate(2012, 2, 24)));
    }

    @Test
    public void verifyPregnancyScheduleWhenEDDIsInThePast() throws SchedulerException {
        mockToday(newDate(2012, 2, 1));
        LocalDate expectedDeliveryDate = newDate(2012, 1, 23);

        enrollmentId = scheduleAlertForDeliveryNotfication(expectedDeliveryDate);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2012, 2, 6), newDate(2012, 2, 13)));
    }

    @Test
    public void verifyPregnancyScheduleWhenEDDIsInOverOneWeekFromToday() throws SchedulerException {
        mockToday(newDate(2012, 2, 1));
        LocalDate expectedDeliveryDate = newDate(2012, 2, 12);
        enrollmentId = scheduleAlertForDeliveryNotfication(expectedDeliveryDate);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2012, 2, 12), newDate(2012, 2, 19), newDate(2012, 2, 26), newDate(2012, 3, 4)));
    }

    @Test
    public void verifyPregnancyScheduleWhenEDDIsOnToday() throws SchedulerException {
        mockToday(newDate(2012, 2, 1));
        LocalDate expectedDeliveryDate = newDate(2012, 2, 2);

        enrollmentId = scheduleAlertForDeliveryNotfication(expectedDeliveryDate);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2012, 2, 2), newDate(2012, 2, 9), newDate(2012, 2, 16), newDate(2012, 2, 23)));
    }

    @Test
    public void verifyPregnancyScheduleWhenEDDIsInTheFarPast() throws SchedulerException {
        mockToday(newDate(2012, 2, 1));
        LocalDate expectedDeliveryDate = newDate(2012, 1, 4);
        enrollmentId = scheduleAlertForDeliveryNotfication(expectedDeliveryDate);

        List<SimpleTrigger> alerts = captureAlertsForNextMilestone(enrollmentId);
        assertAlerts(alerts, new ArrayList<Date>());
    }

    private String scheduleAlertForDeliveryNotfication(LocalDate expectedDeliveryDate) {
        final LocalDate conceptionDate = Pregnancy.basedOnDeliveryDate(expectedDeliveryDate).dateOfConception();
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("123", ScheduleNames.DELIVERY, preferredAlertTime, conceptionDate, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
