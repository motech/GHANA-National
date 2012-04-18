package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestANCEnrollment;
import org.motechproject.ghana.national.functional.data.TestMobileMidwifeEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.OpenMRSDB;
import org.motechproject.ghana.national.functional.framework.ScheduleTracker;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.helper.ScheduleHelper;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.ANCEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.MobileMidwifeEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.domain.IPTDose.SP2;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT2;
import static org.testng.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class RegisterANCMobileUploadTest extends LoggedInUserFunctionalTest {

    @Autowired
    ScheduleTracker scheduleTracker;

    @Autowired
    private OpenMRSDB openMRSDB;

    @Test
    public void shouldCheckForMandatoryFields() {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerANCForm(), MapUtils.EMPTY_MAP);
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("gravida"), hasItem("is mandatory"));
        assertThat(errorsMap.get("parity"), hasItem("is mandatory"));
        assertThat(errorsMap.get("height"), hasItem("is mandatory"));
        assertThat(errorsMap.get("estDeliveryDate"), hasItem("is mandatory"));
        assertThat(errorsMap.get("ancRegNumber"), hasItem("is mandatory"));
    }

    @Test
    public void shouldValidateIfFacilityIdStaffIdMotechIdExists() {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerANCForm(), new HashMap<String, String>() {{
            put("motechId", "-1");
            put("facilityId", "-1");
            put("staffId", "-1");
            put("gravida", "1");
            put("parity", "1");
            put("height", "61");
            put("estDeliveryDate", "2010-12-11");
            put("date", "2012-12-11");
            put("deliveryDateConfirmed", "Y");
            put("regDateToday", "TODAY");
            put("addHistory", "0");
            put("ancRegNumber", "123ABC");
        }});
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("not found"));
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("motechId"), hasItem("not found"));
    }

    @Test
    public void shouldCreateANCWithoutHistoryForAPatientWithMobileDeviceAndSearchForItInWeb() {
        DataGenerator dataGenerator = new DataGenerator();

        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        String patientId = patientGenerator.createPatient(testPatient, browser, homePage);

        Pregnancy pregnancyIn12thWeekOfPregnancy = Pregnancy.basedOnConceptionDate(DateUtil.today().minusWeeks(12));
        LocalDate registrationDate = DateUtil.today();
        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId)
                .withEstimatedDateOfDelivery(pregnancyIn12thWeekOfPregnancy.dateOfDelivery()).withRegistrationDate(registrationDate);

        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId, testPatient.facilityId()).patientId(patientId);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withMobileMidwifeEnrollmentThroughMobile(mmEnrollmentDetails));

        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.displaying(ancEnrollment);

        String openMRSId = openMRSDB.getOpenMRSId(patientId);
        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_DELIVERY, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_DELIVERY).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(TT_VACCINATION, registrationDate),
                scheduleTracker.firstAlertScheduledFor(openMRSId, TT_VACCINATION).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_IPT_VACCINE, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate());

        patientEditPage = toPatientEditPage(testPatient);
        MobileMidwifeEnrollmentPage mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientEditPage);
        assertThat(mobileMidwifeEnrollmentPage.details(), is(equalTo(mmEnrollmentDetails)));
    }


    @Test
    public void shouldCreateANCWithScheduleForIPTAndTT_IfThePatientHasAVaccineHistoryBeforeTheRecentPregnancyConception() {
        DataGenerator dataGenerator = new DataGenerator();
        String staffId = staffGenerator.createStaff(browser, homePage);
        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        String patientId = patientGenerator.createPatient(testPatient, browser, homePage);

        Pregnancy pregnancyIn12thWeekOfPregnancy = Pregnancy.basedOnConceptionDate(DateUtil.today().minusWeeks(12));
        LocalDate registrationDate = DateUtil.today();
        LocalDate iptVaccinationDate = pregnancyIn12thWeekOfPregnancy.dateOfConception().minusMonths(2);
        LocalDate ttVaccinationDate = pregnancyIn12thWeekOfPregnancy.dateOfConception().minusMonths(3);
        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId)
                .withEstimatedDateOfDelivery(pregnancyIn12thWeekOfPregnancy.dateOfDelivery()).withRegistrationDate(registrationDate)
                .withLastIPT("1").withLastIPTDate(iptVaccinationDate)
                .withLastTT("1").withLastTTDate(ttVaccinationDate);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());
        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.displaying(ancEnrollment);

        String openMRSId = openMRSDB.getOpenMRSId(patientId);
        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_DELIVERY, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_DELIVERY).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(TT_VACCINATION, registrationDate),
                scheduleTracker.firstAlertScheduledFor(openMRSId, TT_VACCINATION).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_IPT_VACCINE, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate());
    }

    @Test(enabled = false)
    public void shouldCreateANCWithScheduleForIPTAndTTwithNextMilestones_IfThePatientHasAVaccineHistoryInActivePregnancyPeriod(){
        DataGenerator dataGenerator = new DataGenerator();
        String staffId = staffGenerator.createStaff(browser, homePage);
        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        String patientId = patientGenerator.createPatient(testPatient, browser, homePage);

        Pregnancy in12thWeekOfPregnancy = Pregnancy.basedOnConceptionDate(DateUtil.today().minusWeeks(11));
        LocalDate registrationDate = DateUtil.today();
        LocalDate iptVaccinationDate = in12thWeekOfPregnancy.dateOfConception().plusDays(2);
        LocalDate ttVaccinationDate = in12thWeekOfPregnancy.dateOfConception().plusDays(3);
        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId)
                .withEstimatedDateOfDelivery(in12thWeekOfPregnancy.dateOfDelivery()).withRegistrationDate(registrationDate)
                .withLastIPT("1").withLastIPTDate(iptVaccinationDate)
                .withLastTT("1").withLastTTDate(ttVaccinationDate);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());
        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.displaying(ancEnrollment);

        String openMRSId = openMRSDB.getOpenMRSId(patientId);

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(TT_VACCINATION, ttVaccinationDate, TT2.getScheduleMilestoneName()), TT2.getScheduleMilestoneName(),
                scheduleTracker.firstAlertScheduledFor(openMRSId, TT_VACCINATION).getAlertAsLocalDate(), scheduleTracker.getActiveMilestone(openMRSId, TT_VACCINATION));

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_IPT_VACCINE, iptVaccinationDate, SP2.milestone()), SP2.milestone(),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate(), scheduleTracker.getActiveMilestone(openMRSId, ANC_IPT_VACCINE));
    }

    @Test
    public void shouldCreateANCWithScheduleForOnlyIPTGivenTTHistoryForAPatient() {
        DataGenerator dataGenerator = new DataGenerator();
        String staffId = staffGenerator.createStaff(browser, homePage);
        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.OTHER)
                .estimatedDateOfBirth(false);
        String patientId = patientGenerator.createPatient(testPatient, browser, homePage);

        Pregnancy pregnancyIn12thWeekOfPregnancy = Pregnancy.basedOnConceptionDate(DateUtil.today().minusWeeks(12));
        LocalDate registrationDate = DateUtil.today();
        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId)
                .withEstimatedDateOfDelivery(pregnancyIn12thWeekOfPregnancy.dateOfDelivery()).withRegistrationDate(registrationDate);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());
        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.displaying(ancEnrollment);

        String openMRSId = openMRSDB.getOpenMRSId(patientId);
        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_DELIVERY, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_DELIVERY).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(TT_VACCINATION, registrationDate),
                scheduleTracker.firstAlertScheduledFor(openMRSId, TT_VACCINATION).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_IPT_VACCINE, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate());
    }

    private LocalDate expectedFirstAlertDate(String scheduleName, LocalDate referenceDate) {
        return scheduleTracker.firstAlert(scheduleName, referenceDate);
    }

    private LocalDate expectedFirstAlertDate(String scheduleName, LocalDate referenceDate, String milestoneName) {
        return scheduleTracker.firstAlert(scheduleName, referenceDate, milestoneName);
    }

    @Test
    public void shouldUnRegisterExistingMobileMidWifeWhileANCRegistration() {
        DataGenerator dataGenerator = new DataGenerator();

        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        String patientId = patientGenerator.createPatient(testPatient, browser, homePage);

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        MobileMidwifeEnrollmentPage mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientEditPage);
        mobileMidwifeEnrollmentPage.enroll(TestMobileMidwifeEnrollment.with(staffId));

        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());
        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientPageAfterEdit = toPatientEditPage(testPatient);
        mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientPageAfterEdit);


        assertThat(mobileMidwifeEnrollmentPage.status(), is("INACTIVE"));
    }

    @Test
    public void shouldCreateANCForAPatientWithMobileDeviceAndSearchForItInWeb() {
        DataGenerator dataGenerator = new DataGenerator();

        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        String patientId = patientGenerator.createPatient(testPatient, browser, homePage);

        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId);
        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId, testPatient.facilityId()).patientId(patientId);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withMobileMidwifeEnrollmentThroughMobile(mmEnrollmentDetails));

        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.displaying(ancEnrollment);

        patientEditPage = toPatientEditPage(testPatient);
        MobileMidwifeEnrollmentPage mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientEditPage);
        assertThat(mobileMidwifeEnrollmentPage.details(), is(equalTo(mmEnrollmentDetails)));
    }

    private PatientEditPage toPatientEditPage(TestPatient testPatient) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(testPatient.firstName());
        searchPatientPage.displaying(testPatient);
        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }
}
