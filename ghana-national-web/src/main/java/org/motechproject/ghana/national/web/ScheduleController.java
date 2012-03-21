package org.motechproject.ghana.national.web;

import org.motechproject.MotechException;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Controller
@RequestMapping(value = "/debug/schedule/")
public class ScheduleController {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private AllEnrollments allEnrollments;

    @Autowired
    private AllPatients allPatients;

    private Pattern ALERT_ORDER_INDEX_REGEX = Pattern.compile("^.*\\.(.*?)-repeat$");

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String schedulesOfAPatient(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        Map<String, List<TestAlert>> schedules = null;
        final String patientId = request.getParameter("patientId");
        if (patientId != null) {
            schedules = getAllSchedulesFor(patientId);
        }
        modelMap.put("schedules", schedules);
        return "schedule/search";
    }

    private Map<String, List<TestAlert>> getAllSchedulesFor(final String motechId) {
        Patient patientByMotechId = allPatients.getPatientByMotechId(motechId);
        String patientId = patientByMotechId.getMRSPatientId();
        Map<String, List<TestAlert>> schedules = new HashMap<String, List<TestAlert>>();
        try {
            for (Field field : ScheduleNames.class.getFields()) {
                final String scheduleName = (String) field.get(field);
                Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(patientId, scheduleName);
                if (activeEnrollment != null) {
                    schedules.put(scheduleName, captureAlertsForNextMilestone(activeEnrollment.getId()));
                }
            }
        } catch (Exception e) {
            throw new MotechException("Encountered exception, ", e);
        }
        return schedules;
    }


    private List<TestAlert> captureAlertsForNextMilestone(String enrollmentId) throws SchedulerException {
        final Scheduler scheduler = schedulerFactoryBean.getScheduler();
        final String jobGroupName = MotechSchedulerServiceImpl.JOB_GROUP_NAME;
        String[] jobNames = scheduler.getJobNames(jobGroupName);
        List<TestJobDetail> alertTriggers = new ArrayList<TestJobDetail>();

        for (String jobName : jobNames) {
            if (jobName.contains(format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollmentId))) {
                Trigger[] triggersOfJob = scheduler.getTriggersOfJob(jobName, jobGroupName);
                alertTriggers.add(new TestJobDetail((SimpleTrigger) triggersOfJob[0], scheduler.getJobDetail(jobName, jobGroupName)));
            }
        }
        return createActualTestAlertTimes(alertTriggers);
    }

    private List<TestAlert> createActualTestAlertTimes(List<TestJobDetail> alertsJobDetails) {
        sortBasedOnIndexInAlertName(alertsJobDetails);

        List<TestAlert> actualAlertTimes = new ArrayList<TestAlert>();
        for (TestJobDetail testJobDetail : alertsJobDetails) {
            SimpleTrigger alert = testJobDetail.trigger();
            Date nextFireTime = alert.getNextFireTime();
            JobDataMap dataMap = testJobDetail.getJobDetail().getJobDataMap();
            actualAlertTimes.add(new TestAlert(window(dataMap), nextFireTime));
            for (int i = 1; i <= alert.getRepeatCount(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime((Date) nextFireTime.clone());
                calendar.add(Calendar.DAY_OF_MONTH, toDays(i * alert.getRepeatInterval()));
                actualAlertTimes.add(new TestAlert(window(dataMap), calendar.getTime()));
            }
        }
        return actualAlertTimes;
    }

    private void sortBasedOnIndexInAlertName(List<TestJobDetail> alertJobDetails) {
        Collections.sort(alertJobDetails, new Comparator<TestJobDetail>() {
            @Override
            public int compare(TestJobDetail testJobDetail1, TestJobDetail testJobDetail2) {
                return extractIndexFromAlertName(testJobDetail1.trigger().getName()).compareTo(extractIndexFromAlertName(testJobDetail2.trigger().getName()));
            }
        });
    }

    private Integer extractIndexFromAlertName(String name) {
        Matcher matcher = ALERT_ORDER_INDEX_REGEX.matcher(name);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : null;
    }


    private int toDays(long milliseconds) {
        return (int) (milliseconds / 1000 / 60 / 60 / 24);
    }

    private WindowName window(JobDataMap dataMap) {
        return WindowName.valueOf((String) dataMap.get(EventDataKeys.WINDOW_NAME));
    }

    public class TestAlert {
        private WindowName window;
        private Date alertDate;

        public TestAlert(WindowName window, Date alertDate) {
            this.window = window;
            this.alertDate = alertDate;
        }

        public WindowName getWindow() {
            return window;
        }

        public Date getAlertDate() {
            return alertDate;
        }

        @Override
        public String toString() {
            return "TestAlert{" +
                    "window=" + window +
                    ", alertDate=" + alertDate +
                    '}';
        }
    }

    public class TestJobDetail {

        SimpleTrigger trigger;
        JobDetail jobDetail;

        public TestJobDetail(SimpleTrigger trigger, JobDetail jobDetail) {
            this.trigger = trigger;
            this.jobDetail = jobDetail;
        }

        public JobDetail getJobDetail() {
            return jobDetail;
        }

        public SimpleTrigger trigger() {
            return trigger;
        }
    }

}
