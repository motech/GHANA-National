package org.motechproject.ghana.national.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.*;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.util.DateUtil;
import org.openmrs.patient.UnallowedIdentifierException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.motechproject.util.StringUtil.isNullOrEmpty;

@Controller
@RequestMapping(value = "/debug/schedule/")
public class TestDataGenController {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private AllEnrollments allEnrollments;

    @Autowired
    private PatientService patientService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private FacilityService facilityService;

    private Pattern ALERT_ORDER_INDEX_REGEX = Pattern.compile("^.*\\.(.*?)-repeat$");

    private AtomicInteger firstNameIndexCounter = new AtomicInteger(0);

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String schedulesOfAPatientByMotechId(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {

        HashMap<String, Map<String, List<Alert>>> schedules = null;
        final String patientId = request.getParameter("patientId");
        if (patientId != null) {
            schedules = new HashMap<String, Map<String, List<Alert>>>() {{
                put(patientId, getAllSchedulesByMotechId(patientId));
            }};
        }
        modelMap.put("patientSchedules", schedules);
        modelMap.put("firstNameIndexCounter", firstNameIndexCounter.incrementAndGet());
        return "schedule/search";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String createPatient(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("regDate") String regDate,
                                @RequestParam("patientType") String patientType,
                                @RequestParam("patientDate") String patientDate,
                                ModelMap modelMap) throws UnsupportedEncodingException, ParseException, UserAlreadyExistsException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {

        Date dateOfBirth = patientType.equals("Mother") ? DateUtil.newDate(1980, 1, 1).toDate() : new SimpleDateFormat("dd/MM/yyyy").parse(patientDate);

        String facilityId = facilityService.searchFacilities(null, "Ashanti", "Ghana", "Ashanti", null, null).get(0).getMrsFacilityId();
        patientService.registerPatient(patient(firstName, lastName, dateOfBirth, facilityId), createStaff(firstName));
        return "schedule/search";
    }

    @RequestMapping(value = "searchByOpenmrsId", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String schedulesOfAPatientByOpenmrsId(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        HashMap<String, Map<String, List<Alert>>> schedules = null;
        final String patientId = request.getParameter("patientId");
        if (patientId != null) {
            schedules = new HashMap<String, Map<String, List<Alert>>>() {{
                put(patientId, getAllSchedulesByMrsId(patientId));
            }};
        }
        modelMap.put("patientSchedules", schedules);
        return "schedule/search";
    }

    @RequestMapping(value = "convertPatientId", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String convertPatientId(@RequestParam("mrsId") String mrsId, @RequestParam("motechId") String motechId, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        if (!isNullOrEmpty(mrsId)) {
            motechId = patientService.patientByOpenmrsId(mrsId).getMotechId();
        } else if (!isNullOrEmpty(motechId)) {
            mrsId = patientService.getPatientByMotechId(motechId).getMRSPatientId();
        }
        if (!isNullOrEmpty(mrsId) && !isNullOrEmpty(motechId))
            modelMap.put("ids", "(MoTeCH id) " + motechId + " = " + mrsId + " (OpenMRS id)");
        return "schedule/search";
    }

    @RequestMapping(value = "searchWithinRange", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String schedulesWithinRange(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        Map<String, Map<String, List<Alert>>> schedules = getAllActiveSchedules();
        ;
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        filterActiveSchedulesWithin(schedules, formatter.parse(startDate), DateUtil.newDate(formatter.parse(endDate)).plusDays(1).toDate());
        modelMap.put("patientSchedules", schedules);
        modelMap.put("startDate", startDate);
        modelMap.put("endDate", endDate);
        return "schedule/search";
    }


    private String createStaff(String firstName) throws UserAlreadyExistsException {
        final String roleOfStaff = StaffType.Role.COMMUNITY_HEALTH_NURSE.key();
        MRSPerson mrsPerson = new MRSPerson().firstName(firstName).lastName("LastName")
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, getPhoneNumber("1", firstNameIndexCounter.get())))
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, roleOfStaff));
        return staffService.saveUser(new MRSUser().person(mrsPerson).securityRole(StaffType.Role.securityRoleFor(roleOfStaff))).getSystemId();
    }

    private Patient patient(String firstName, String lastName, Date dob, String facilityId) throws UnallowedIdentifierException {
        final boolean hasBeenInsured = false;
        Boolean isDateOfBirthEstimated = true;
        final String gender = "F";

        List<Attribute> attributes = new ArrayList<Attribute>();
        setAttribute(attributes, String.valueOf(hasBeenInsured), PatientAttributes.INSURED);

        setAttribute(attributes, getPhoneNumber("0", firstNameIndexCounter.get()), PatientAttributes.PHONE_NUMBER);

        MRSPerson mrsPerson = new MRSPerson().firstName(firstName).middleName("middleName")
                .lastName(lastName).dateOfBirth(dob).birthDateEstimated(isDateOfBirthEstimated)
                .gender(gender).attributes(attributes);

        return new Patient(new MRSPatient(null, mrsPerson, new MRSFacility(facilityId)));
    }

    private String getPhoneNumber(String fillDigit, int uniqueId) {
        final int paddingLength = 9 - String.valueOf(uniqueId).length();
        return String.valueOf(String.format("0%" + fillDigit + paddingLength + "d%d", 0, uniqueId));
    }

    private void setAttribute(List<Attribute> attributes, String attributeValue, PatientAttributes patientAttribute) {
        if (StringUtils.isNotEmpty(attributeValue)) {
            attributes.add(new Attribute(patientAttribute.getAttribute(), attributeValue));
        }
    }


    private void filterActiveSchedulesWithin(Map<String, Map<String, List<Alert>>> patientSchedules, Date startDate, Date endDate) {
        for (Map<String, List<Alert>> schedules : patientSchedules.values()) {
            for (Map.Entry<String, List<Alert>> scheduleEntry : schedules.entrySet()) {
                for (Alert alert : scheduleEntry.getValue()) {
                    if (alert.getAlertDate().after(endDate) || alert.getAlertDate().before(startDate)) {
                        scheduleEntry.getValue().remove(alert);
                    }
                }
                if (scheduleEntry.getValue().size() == 0) {
                    schedules.remove(scheduleEntry.getKey());
                }
            }
        }
    }

    private Map<String, Map<String, List<Alert>>> getAllActiveSchedules() {
        final List<Enrollment> enrollments = allEnrollments.getAll();
        Map<String, Map<String, List<Alert>>> schedules = new HashMap<String, Map<String, List<Alert>>>();
        for (Enrollment enrollment : enrollments) {
            if (EnrollmentStatus.ACTIVE.equals(enrollment.getStatus())) {
                final Map<String, List<Alert>> alerts = getAllSchedulesByMrsPatientId(enrollment.getExternalId());
                schedules.put(patientService.patientByOpenmrsId(enrollment.getExternalId()).getMotechId(), alerts);
            }
        }
        return schedules;
    }

    private Map<String, List<Alert>> getAllSchedulesByMotechId(final String patientId) {
        Patient patientByMotechId = patientService.getPatientByMotechId(patientId);
        return getAllSchedulesByMrsId(patientByMotechId.getMRSPatientId());
    }

    private Map<String, List<Alert>> getAllSchedulesByMrsId(String mrsPatientId) {
        return getAllSchedulesByMrsPatientId(mrsPatientId);
    }

    private Map<String, List<Alert>> getAllSchedulesByMrsPatientId(String patientId) {
        Map<String, List<Alert>> schedules = new ConcurrentHashMap<String, List<Alert>>();
        try {
            for (Field field : ScheduleNames.class.getFields()) {
                final String scheduleName = (String) field.get(field);
                Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(patientId, scheduleName);
                if (activeEnrollment != null) {
                    schedules.put(scheduleName + "(" + activeEnrollment.getCurrentMilestoneName() + ")", captureAlertsForNextMilestone(activeEnrollment.getId()));
                }
            }
        } catch (Exception e) {
            throw new MotechException("Encountered exception, ", e);
        }
        return schedules;
    }


    private List<Alert> captureAlertsForNextMilestone(String enrollmentId) throws SchedulerException {
        final Scheduler scheduler = schedulerFactoryBean.getScheduler();
        final String jobGroupName = MotechSchedulerServiceImpl.JOB_GROUP_NAME;
        String[] jobNames = scheduler.getJobNames(jobGroupName);
        List<JobDetail> alertTriggers = new ArrayList<JobDetail>();

        for (String jobName : jobNames) {
            if (jobName.contains(format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollmentId))) {
                Trigger[] triggersOfJob = scheduler.getTriggersOfJob(jobName, jobGroupName);
                alertTriggers.add(new JobDetail((SimpleTrigger) triggersOfJob[0], scheduler.getJobDetail(jobName, jobGroupName)));
            }
        }
        return createActualTestAlertTimes(alertTriggers);
    }

    private List<Alert> createActualTestAlertTimes(List<JobDetail> alertsJobDetails) {
        sortBasedOnIndexInAlertName(alertsJobDetails);

        List<Alert> actualAlertTimes = new CopyOnWriteArrayList<Alert>();
        for (JobDetail jobDetail : alertsJobDetails) {
            SimpleTrigger alert = jobDetail.trigger();
            Date nextFireTime = alert.getNextFireTime();
            JobDataMap dataMap = jobDetail.getJobDetail().getJobDataMap();
            actualAlertTimes.add(new Alert(window(dataMap), nextFireTime));
            for (int i = 1; i <= alert.getRepeatCount(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime((Date) nextFireTime.clone());
                calendar.add(Calendar.DAY_OF_MONTH, toDays(i * alert.getRepeatInterval()));
                actualAlertTimes.add(new Alert(window(dataMap), calendar.getTime()));
            }
        }
        return actualAlertTimes;
    }

    private void sortBasedOnIndexInAlertName(List<JobDetail> alertJobDetails) {
        Collections.sort(alertJobDetails, new Comparator<JobDetail>() {
            @Override
            public int compare(JobDetail jobDetail1, JobDetail jobDetail2) {
                return extractIndexFromAlertName(jobDetail1.trigger().getName()).compareTo(extractIndexFromAlertName(jobDetail2.trigger().getName()));
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

    public class Alert {
        private WindowName window;
        private Date alertDate;

        public Alert(WindowName window, Date alertDate) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Alert)) return false;

            Alert alert = (Alert) o;

            if (alertDate != null ? !alertDate.equals(alert.alertDate) : alert.alertDate != null) return false;
            if (window != alert.window) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = window != null ? window.hashCode() : 0;
            result = 31 * result + (alertDate != null ? alertDate.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Alert{" +
                    "window=" + window +
                    ", alertDate=" + alertDate +
                    '}';
        }
    }

    public class JobDetail {

        SimpleTrigger trigger;
        org.quartz.JobDetail jobDetail;

        public JobDetail(SimpleTrigger trigger, org.quartz.JobDetail jobDetail) {
            this.trigger = trigger;
            this.jobDetail = jobDetail;
        }

        public org.quartz.JobDetail getJobDetail() {
            return jobDetail;
        }

        public SimpleTrigger trigger() {
            return trigger;
        }
    }

}
