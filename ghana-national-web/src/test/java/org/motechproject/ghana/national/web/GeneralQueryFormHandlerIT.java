package org.motechproject.ghana.national.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.ghana.national.bean.GeneralQueryForm;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.GeneralQueryType;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.FacilityAlreadyFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.ghana.national.handlers.GeneralQueryFormHandler;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.security.OpenMRSSession;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentsQueryService;
import org.motechproject.util.DateUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_DELIVERY;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_0;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_OPV_OTHERS;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_PENTA;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_CHILD_2;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_CHILD_3;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_MOTHER_2;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_MOTHER_3;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.scheduletracking.api.domain.WindowName.earliest;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class GeneralQueryFormHandlerIT {
    @Autowired
    EnrollmentsQueryService enrollmentsQueryService;

    @Mock
    SMSGateway mockSMSGateway;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    AllCareSchedules allCareSchedules;

    @Autowired
    private PatientService patientService;

    private GeneralQueryFormHandler generalQueryFormHandler;

    @Value("#{openmrsProperties['openmrs.admin.username']}")
    private String userName;

    @Value("#{openmrsProperties['openmrs.admin.password']}")
    private String password;
    @Autowired
    private OpenMRSSession openMRSSession;

    @Autowired
    private AllEnrollments allEnrollments;

    @Autowired
    private AllSchedules allTrackedSchedules;

    @Autowired
    private AllAppointments allAppointments;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    Facility facility1;
    Facility facility2;
    Patient patient1234560_InFacility1;
    Patient patient1234562_InFacility1;
    Patient patient1234565_InFacility1;
    Patient patient1234561_InFacility2;
    Patient patient1234568_InFacility1;
    Patient patient1234563_InFacility2;
    Patient patient1234564_InFacility2;
    Patient patient1234567_InFacility2;


    Patient patient1234566_InFacility2;
    QueryTestDataProvider testData;
    Map<String, Patient> patientMap = new HashMap<String, Patient>();

    @Before
    public void setUp() throws FacilityAlreadyFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        initMocks(this);
        generalQueryFormHandler = new GeneralQueryFormHandler();
        ReflectionTestUtils.setField(generalQueryFormHandler, "smsGateway", mockSMSGateway);
        ReflectionTestUtils.setField(generalQueryFormHandler, "enrollmentsQueryService", enrollmentsQueryService);
        ReflectionTestUtils.setField(generalQueryFormHandler, "appointmentService", appointmentService);
        ReflectionTestUtils.setField(generalQueryFormHandler, "facilityService", facilityService);
        ReflectionTestUtils.setField(generalQueryFormHandler, "patientService", patientService);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(OpenMRSSession.login(userName, password), password));
        openMRSSession.open();
        openMRSSession.authenticate();

        if (testData == null) {
            testData = new QueryTestDataProvider(allTrackedSchedules, allCareSchedules, patientService);

            facility1 = createFacility("Newfacility1" + new DataGenerator().randomString(5), new DataGenerator().randomPhoneNumber());
            facility2 = createFacility("Newfacility2" + new DataGenerator().randomString(5), new DataGenerator().randomPhoneNumber());
            patient1234560_InFacility1 = createPatientAndAddToMap("1234560", facility1);
            patient1234562_InFacility1 = createPatientAndAddToMap("1234562", facility1);
            patient1234565_InFacility1 = createPatientAndAddToMap("1234565", facility1);
            patient1234568_InFacility1 = createPatientAndAddToMap("1234568", facility1);
            patient1234561_InFacility2 = createPatientAndAddToMap("1234561", facility2);
            patient1234563_InFacility2 = createPatientAndAddToMap("1234563", facility2);
            patient1234564_InFacility2 = createPatientAndAddToMap("1234564", facility2);
            patient1234566_InFacility2 = createPatientAndAddToMap("1234566", facility2);
            patient1234567_InFacility2 = createPatientAndAddToMap("1234567", facility2);
        }
    }

    private Patient createPatientAndAddToMap(String motechId, Facility facility) throws PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        Patient patient = createPatient(motechId, facility);
        patientMap.put(motechId, patient);
        return patient;
    }

    @Test
    public void shouldReturnUpcomingDeliveries() throws PatientIdIncorrectFormatException, PatientIdNotUniqueException, FacilityAlreadyFoundException {
        LocalDate referenceDate1234560 = DateUtil.today().plusDays(3).minusWeeks(40);
        enrollToASchedule(patient1234560_InFacility1, referenceDate1234560, ScheduleNames.ANC_DELIVERY.getName());

        LocalDate referenceDate1234562 = DateUtil.today().minusDays(1).minusWeeks(40);
        enrollToASchedule(patient1234562_InFacility1, referenceDate1234562, ScheduleNames.ANC_DELIVERY.getName());

        LocalDate referenceDate1234561 = DateUtil.today().plusDays(6).minusWeeks(40);
        enrollToASchedule(patient1234561_InFacility2, referenceDate1234561, ScheduleNames.ANC_DELIVERY.getName());

        LocalDate referenceDate1234563 = DateUtil.today().plusDays(8).minusWeeks(40);
        enrollToASchedule(patient1234563_InFacility2, referenceDate1234563, ScheduleNames.ANC_DELIVERY.getName());

        String responsePhoneNumber = "0542319876";

        String message = submitForFacility(responsePhoneNumber, GeneralQueryType.UPCOMING_DELIVERIES,
                facility1);
        assertMessageFor("1234560", "Delivery", message);
        assertNoMessageFor(asList("1234562"), "Delivery", message);

        responsePhoneNumber = "0901823765";
        message = submitForFacility(responsePhoneNumber, GeneralQueryType.UPCOMING_DELIVERIES,
                facility2);
        assertMessageFor("1234561", "Delivery", message);
        assertNoMessageFor(asList("1234563"), "Delivery", message);
    }

    @Test
    public void shouldReturnRecentDeliveries() throws PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        testData.addToEarlyWindow(patient1234560_InFacility1, ANC_DELIVERY.getName(), null);
        testData.addToEarlyWindow(patient1234561_InFacility2, ANC_DELIVERY.getName(), null);

        testData.addToDueWindow(patient1234562_InFacility1, ANC_DELIVERY.getName(), null);
        testData.addToDueWindow(patient1234563_InFacility2, ANC_DELIVERY.getName(), null);
        testData.addToDueWindow(patient1234564_InFacility2, ANC_DELIVERY.getName(), null);

        testData.addToLateWindow(patient1234565_InFacility1, ANC_DELIVERY.getName(), null);

        List<String> patientToBeFulfilled = asList("1234562", "1234563", "1234564", "1234565");

        testData.fulfill(patientToBeFulfilled, ANC_DELIVERY.getName(), DateUtil.today());

        String responsePhoneNumber = "0542319876";

        String message = submitForFacility(responsePhoneNumber, GeneralQueryType.RECENT_DELIVERIES, facility1);
        assertMessageFor("1234562", "Delivery", message);
        assertMessageFor("1234565", "Delivery", message);
        assertNoMessageFor(asList("1234560"), "Delivery", message);

        responsePhoneNumber = "0901823765";
        message = submitForFacility(responsePhoneNumber, GeneralQueryType.RECENT_DELIVERIES, facility2);
        assertMessageFor("1234563", "Delivery", message);
        assertMessageFor("1234564", "Delivery", message);
        assertNoMessageFor(asList("1234561"), "Delivery", message);
    }

    @Test
    public void shouldSendNoPatientsForQueryTypeInResponse() throws PatientIdIncorrectFormatException, PatientIdNotUniqueException {

        final GeneralQueryForm generalQueryForm = new GeneralQueryForm();
        generalQueryForm.setQueryType(GeneralQueryType.TT_DEFAULTERS);
        String responsePhoneNumber = "0987654321";
        generalQueryForm.setResponsePhoneNumber(responsePhoneNumber);
        generalQueryForm.setFacilityId(facility1.motechId());
        generalQueryFormHandler.handleFormEvent(new MotechEvent("subject", new HashMap<String, Object>() {{
            put("formBean", generalQueryForm);
        }}));
        ArgumentCaptor<String> messageCaptor=ArgumentCaptor.forClass(String.class);
        verify(mockSMSGateway).dispatchSMS(eq(responsePhoneNumber), messageCaptor.capture());
        assertTrue(messageCaptor.getValue().contains(String.format("No Patients found for " + generalQueryForm.getQueryType())));
    }

    @Test
    public void shouldReturnOverDueDeliveries() throws PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        testData.addToEarlyWindow(patient1234560_InFacility1, ANC_DELIVERY.getName(), null);
        testData.addToEarlyWindow(patient1234561_InFacility2, ANC_DELIVERY.getName(), null);

        testData.addToDueWindow(patient1234562_InFacility1, ANC_DELIVERY.getName(), null);
        testData.addToDueWindow(patient1234563_InFacility2, ANC_DELIVERY.getName(), null);


        testData.addToLateWindow(patient1234565_InFacility1, ANC_DELIVERY.getName(), null);
        testData.addToLateWindow(patient1234566_InFacility2, ANC_DELIVERY.getName(), null);

        String responsePhoneNumber = "0542319876";
        String message = submitForFacility(responsePhoneNumber,
                GeneralQueryType.OVERDUE_DELIVERIES, facility1);
        for (String motechId : QueryTestDataProvider.getPatientIdsInWindowsForSchedules(asList(late.name()), facility1.getMrsFacilityId(), ScheduleNames.ANC_DELIVERY.getName())) {
            assertMessageFor(motechId, "Delivery", message);
        }
        assertNoMessageFor(QueryTestDataProvider.getPatientIdsInWindowsForSchedules(asList(earliest.name(), due.name()), facility1.getMrsFacilityId(), ScheduleNames.ANC_DELIVERY.getName()), "Delivery", message);

        responsePhoneNumber = "0901823765";
        message = submitForFacility(responsePhoneNumber, GeneralQueryType.OVERDUE_DELIVERIES, facility2);
        for (String motechId : QueryTestDataProvider.getPatientIdsInWindowsForSchedules(asList(late.name()), facility2.getMrsFacilityId(), ScheduleNames.ANC_DELIVERY.getName())) {
            assertMessageFor(motechId, "Delivery", message);
        }
        assertNoMessageFor(QueryTestDataProvider.getPatientIdsInWindowsForSchedules(asList(earliest.name(), due.name()), facility1.getMrsFacilityId(), ScheduleNames.ANC_DELIVERY.getName()), "Delivery", message);
    }

    @Test
    public void shouldReturnTTDefaulters() {
        testData.addToLateWindow(patient1234560_InFacility1, TT_VACCINATION.getName(), null);
        testData.addToDueWindow(patient1234562_InFacility1, TT_VACCINATION.getName(), null);
        testData.addToDueWindow(patient1234565_InFacility1, TT_VACCINATION.getName(), null);
        testData.addToLateWindow(patient1234561_InFacility2, TT_VACCINATION.getName(), "TT2");
        testData.addToDueWindow(patient1234567_InFacility2, TT_VACCINATION.getName(), "TT2");
        testData.addToDueWindow(patient1234566_InFacility2, TT_VACCINATION.getName(), "TT2");
        testData.addToLateWindow(patient1234563_InFacility2, TT_VACCINATION.getName(), "TT2");

        String responsePhoneNumber = "0542319876";
        String message = submitForFacility(responsePhoneNumber,
                GeneralQueryType.TT_DEFAULTERS, facility1);
        assertMessageFor("1234560", "TT1", message);
        assertNoMessageFor(asList("1234562", "1234565"), "TT1", message);

        responsePhoneNumber = "0901823765";
        message = submitForFacility(responsePhoneNumber,
                GeneralQueryType.TT_DEFAULTERS, facility2);

        assertMessageFor("1234561", "TT2", message);
        assertMessageFor("1234563", "TT2", message);
        assertNoMessageFor(asList("1234567", "1234566"), "TT2", message);
    }

    @Test
    public void shouldReturnANCDefaulters() {
        testData.addToLateWindow(patient1234560_InFacility1, ScheduleNames.ANC_IPT_VACCINE.getName(), "IPT2");
        testData.addToDueWindow(patient1234562_InFacility1, ScheduleNames.ANC_IPT_VACCINE.getName(), null);
        testData.addToLateWindow(patient1234562_InFacility1, ScheduleNames.TT_VACCINATION.getName(), "TT2");
        testData.addToDueWindow(patient1234560_InFacility1, ScheduleNames.TT_VACCINATION.getName(), null);

        DateTime referenceDateTime=newDateTime(DateUtil.today());

        fixAppointmentForANCVisitOn(referenceDateTime,patient1234560_InFacility1);
        fixAppointmentForANCVisitOn(referenceDateTime.minusWeeks(1),patient1234562_InFacility1);
        fixAppointmentForANCVisitOn(referenceDateTime.minusWeeks(2),patient1234565_InFacility1);
        fixAppointmentForANCVisitOn(referenceDateTime.minusWeeks(3),patient1234568_InFacility1);
        fixAppointmentForANCVisitOn(referenceDateTime.plusDays(1),patient1234561_InFacility2);
        fixAppointmentForANCVisitOn(referenceDateTime.minusWeeks(4),patient1234563_InFacility2);


        String responsePhoneNumber = "0542319876";
        String message = submitForFacility(responsePhoneNumber,
                GeneralQueryType.ANC_DEFAULTERS, facility1);

        assertMessageFor("1234560", "IPT2", message);
        assertMessageFor("1234562", "TT2", message);
        assertMessageFor("1234560","ANCVISIT",message);
        assertMessageFor("1234562","ANCVISIT",message);
        assertMessageFor("1234565","ANCVISIT",message);
        assertMessageFor("1234568","ANCVISIT",message);
        assertNoMessageFor(asList("1234562"), "IPT1", message);
        assertNoMessageFor(asList("1234560"), "TT1", message);

        responsePhoneNumber = "0901823765";
        message=submitForFacility(responsePhoneNumber,
                GeneralQueryType.ANC_DEFAULTERS,facility2);

        assertTrue(message,message.contains(String.format("No Patients found for " + GeneralQueryType.ANC_DEFAULTERS,message)));


    }

    @Test
    public void shouldReturnPNCChildDefaulters() {
        testData.addToEarlyWindow(patient1234560_InFacility1, PNC_CHILD_2.getName(), null);
        testData.addToLateWindow(patient1234562_InFacility1, PNC_CHILD_2.getName(), null);
        testData.addToMaxWindow(patient1234565_InFacility1, PNC_CHILD_3.getName(), null);

        String responsePhoneNumber = "0542319876";
        GeneralQueryType generalQueryType = GeneralQueryType.PNC_C_DEFAULTERS;
        String message = submitForFacility(responsePhoneNumber,
                generalQueryType, facility1);
        assertMessageFor("1234562", allTrackedSchedules.getByName(PNC_CHILD_2.getName()).getFirstMilestone().getName(), message);
        assertMessageFor("1234565", allTrackedSchedules.getByName(PNC_CHILD_3.getName()).getFirstMilestone().getName(), message);
        assertNoMessageFor(asList("1234560"), "PNC-C2", message);
    }

    @Test
    public void shouldReturnPNCMotherDefaulters() {
        testData.addToEarlyWindow(patient1234560_InFacility1, PNC_MOTHER_2.getName(), null);
        testData.addToLateWindow(patient1234562_InFacility1, PNC_MOTHER_2.getName(), null);
        testData.addToMaxWindow(patient1234565_InFacility1, PNC_MOTHER_3.getName(), null);

        String responsePhoneNumber = "0542319876";
        GeneralQueryType generalQueryType = GeneralQueryType.PNC_M_DEFAULTERS;
        String message = submitForFacility(responsePhoneNumber,
                generalQueryType, facility1);
        assertMessageFor("1234562", allTrackedSchedules.getByName(PNC_MOTHER_2.getName()).getFirstMilestone().getName(), message);
        assertMessageFor("1234565", allTrackedSchedules.getByName(PNC_MOTHER_3.getName()).getFirstMilestone().getName(), message);
        assertNoMessageFor(asList("1234560"), "PNC-M2", message);
    }

    @Test
    public void shouldReturnCWCDefaulters() {
        testData.addToEarlyWindow(patient1234560_InFacility1, CWC_IPT_VACCINE.getName(), null);
        testData.addToLateWindow(patient1234562_InFacility1, CWC_IPT_VACCINE.getName(), "IPTi2");
        testData.addToDueWindow(patient1234562_InFacility1, CWC_OPV_0.getName(), null);
        testData.addToLateWindow(patient1234560_InFacility1, CWC_OPV_OTHERS.getName(), "OPV3");
        testData.addToDueWindow(patient1234560_InFacility1, CWC_PENTA.getName(), null);
        testData.addToLateWindow(patient1234565_InFacility1, CWC_PENTA.getName(), "Penta2");

        String responsePhoneNumber = "0542319876";
        GeneralQueryType generalQueryType = GeneralQueryType.CWC_DEFAULTERS;
        String message = submitForFacility(responsePhoneNumber,
                generalQueryType, facility1);
        assertTrue(message,message.contains("List of CWC_DEFAULTERS-IPTi,OPV0,OPV1,OPV2,OPV3,Penta"));
        assertMessageFor("1234562", "IPTi2", message);
        assertNoMessageFor(asList("1234560"), "IPTi1", message);
        assertNoMessageFor(asList("1234562"), "OPV0", message);
        assertMessageFor("1234560", "OPV3", message);
        assertNoMessageFor(asList("1234560"), "Penta1", message);
        assertMessageFor("1234565", "Penta2", message);
    }


    private void enrollToASchedule(Patient patient, LocalDate referenceDate, String scheduleName) {
        allCareSchedules.enroll(new EnrollmentRequest(patient.getMRSPatientId(), scheduleName, null,
                referenceDate, null, null, null, null, patient.facilityMetaData()));
    }

    private String submitForFacility(String responsePhoneNumber, GeneralQueryType generalQueryType, Facility facility) {
        final GeneralQueryForm generalQueryForm = new GeneralQueryForm();
        generalQueryForm.setQueryType(generalQueryType);
        generalQueryForm.setResponsePhoneNumber(responsePhoneNumber);
        generalQueryForm.setFacilityId(facility.motechId());
        MotechEvent event = new MotechEvent("form.validation.successful.NurseQuery.GeneralQuery", new HashMap<String, Object>() {{
            put("formBean", generalQueryForm);
        }});
        generalQueryFormHandler.handleFormEvent(event);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSMSGateway).dispatchSMS(eq(responsePhoneNumber), messageCaptor.capture());
        String message = messageCaptor.getValue();

        return message;

    }

     private void fixAppointmentForANCVisitOn(DateTime referenceDateTime, Patient patient){
          allAppointments.updateANCVisitSchedule(patient, referenceDateTime);
    }

    private void assertNoMessageFor(List<String> notInList, String milestoneName, String message) {
        for (String motechId : notInList) {
            String expectedMessage = buildMessage(motechId);
            expectedMessage = expectedMessage + milestoneName;
            assertFalse(message, message.contains(expectedMessage));
        }
    }

    private void assertMessageFor(String motechId, String milestoneName, String message) {
            String expectedMessage = buildMessage(motechId);
            expectedMessage = expectedMessage + milestoneName;
            assertTrue(message, message.contains(expectedMessage));
    }

    private String buildMessage(String motechId) {
        Patient patient = patientMap.get(motechId);
        return String.format("%s %s, %s", patient.getFirstName(), patient.getLastName(), motechId);
    }

    private Patient createPatient(String motechId, final Facility facility) throws PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        MRSPerson person = new MRSPerson().firstName("patient").dateOfBirth(DateUtil.now().minusYears(21).toDate()).gender("F").birthDateEstimated(true);
        final Patient patient = new Patient(new MRSPatient(motechId, person, facility.mrsFacility()));
        return patientService.registerPatient(patient, "719", new Date());
    }

    private Facility createFacility(String facilityName, String phoneNumber) throws FacilityAlreadyFoundException {
        String anotherFacilityId = facilityService.create(facilityName, "Ghana", "Awutu", "Senya", "Ashanti", phoneNumber, null, null, null);
        return facilityService.getFacility(anotherFacilityId);
    }

    @After
    public void tearDown() throws SchedulerException {
        allEnrollments.removeAll();
        for (Patient patient : patientMap.values()) {
            allAppointments.remove(patient);
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        for (String jobGroup : scheduler.getJobGroupNames()) {
            for (String jobName : scheduler.getJobNames(jobGroup)) {
                scheduler.deleteJob(jobName, jobGroup);
            }
        }

        openMRSSession.close();
    }
}
