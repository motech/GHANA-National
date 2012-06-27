package org.motechproject.ghana.national.handler;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.configuration.BaseScheduleTrackingTest;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.util.DateUtil;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MobileMidwifeIT extends BaseIntegrationTest{
    @Autowired
    IdentifierGenerator identifierGenerator;
    @Autowired
    private MobileMidwifeService service;
    @Autowired
    private AllCampaigns allCampaigns;
    private BaseScheduleTrackingTest baseScheduleTrackingTest = new BaseScheduleTrackingTest() {
    };
    @Autowired
    private AllEnrollments allEnrollments;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Test
    public void shouldAutomaticallyRolloverPregnancyScheduleToChildCare() throws InterruptedException {

        baseScheduleTrackingTest.mockDate(DateUtil.newDateTime(DateUtil.newDate(2012, 6, 28)));
        Medium medium = Medium.SMS;
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().patientId(identifierGenerator.newPatientId()).
                facilityId("23435").staffId("1234").consent(true).serviceType(ServiceType.PREGNANCY).
                learnedFrom(LearnedFrom.GHS_NURSE).language(Language.EN).medium(medium).reasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH).
                messageStartWeek("39").phoneOwnership(PhoneOwnership.PERSONAL).phoneNumber("0987654321")
                .active(true).build();
        String patientId = enrollment.getPatientId();
        service.register(enrollment);

        baseScheduleTrackingTest.mockDate(DateUtil.newDateTime(DateUtil.newDate(2012, 7, 9)));

        MobileMidwifeEnrollment mobileMidwifeEnrollment = service.findActiveBy(patientId);
        assertThat(mobileMidwifeEnrollment.getServiceType(),is(equalTo(ServiceType.CHILD_CARE)));
    }

    @After
    public void deleteAllJobs() throws SchedulerException {

        for (Enrollment enrollment : allEnrollments.getAll()) {
            allEnrollments.remove(enrollment);
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        for (String jobGroup : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroup))) {
                scheduler.deleteJob(jobKey);
            }
        }
    }
}
