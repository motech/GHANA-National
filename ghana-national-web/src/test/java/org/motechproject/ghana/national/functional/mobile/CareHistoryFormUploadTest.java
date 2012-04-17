package org.motechproject.ghana.national.functional.mobile;

import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestANCEnrollment;
import org.motechproject.ghana.national.functional.data.TestCareHistory;
import org.motechproject.ghana.national.functional.framework.OpenMRSDB;
import org.motechproject.ghana.national.functional.framework.ScheduleTracker;
import org.motechproject.ghana.national.functional.helper.ScheduleHelper;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertNull;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.ghana.national.domain.IPTDose.SP2;
import static org.motechproject.util.DateUtil.today;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class CareHistoryFormUploadTest extends LoggedInUserFunctionalTest {

    @Autowired
    ScheduleTracker scheduleTracker;
    @Autowired
    private OpenMRSDB openMRSDB;

    @Test
    public void shouldCreateSchedulesWhileHistoryFormUploadIfThereAreNoActiveSchedulesAndIfTheHistoryIsAssociatedWithTheCurrentPregnancy() {
        String staffId = staffGenerator.createStaff(browser, homePage);
        String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);
        String openMRSId = openMRSDB.getOpenMRSId(patientId);
        LocalDate registrationDate = DateUtil.today();

        Pregnancy pregnancyIn12thWeekOfPregnancy = Pregnancy.basedOnConceptionDate(DateUtil.today().minusWeeks(22));

        TestANCEnrollment ancEnrollment = TestANCEnrollment.createWithoutHistory().withMotechPatientId(patientId).withStaffId(staffId)
                .withEstimatedDateOfDelivery(pregnancyIn12thWeekOfPregnancy.dateOfDelivery()).withRegistrationDate(registrationDate);

        mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());

        final LocalDate vaccineHistoryDate = registrationDate.minusWeeks(5);

        TestCareHistory careHistory = TestCareHistory.withoutHistory(patientId).staffId(staffId)
                .withIPT("1", vaccineHistoryDate).withTT("2", vaccineHistoryDate).withDate(registrationDate);

        mobile.upload(MobileForm.careHistoryForm(), careHistory.forMobile());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(SP2.milestone(), ANC_IPT_VACCINE, vaccineHistoryDate),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate());

    }

    @Test
    public void shouldCreateSchedulesWhileHistoryFormUploadOnlyIfThereAreNoActiveSchedulesDuringANCHistoryUpload() {
        String staffId = staffGenerator.createStaff(browser, homePage);
        String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);
        String openMRSId = openMRSDB.getOpenMRSId(patientId);
        LocalDate registrationDate = DateUtil.today();

        Pregnancy pregnancyIn12thWeekOfPregnancy = Pregnancy.basedOnConceptionDate(DateUtil.today().minusWeeks(12));
        TestANCEnrollment ancEnrollment = TestANCEnrollment.createWithoutHistory().withMotechPatientId(patientId).withStaffId(staffId)
                .withEstimatedDateOfDelivery(pregnancyIn12thWeekOfPregnancy.dateOfDelivery()).withRegistrationDate(registrationDate).addHistoryDetails();

        mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());


        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_IPT_VACCINE, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(TT_VACCINATION, registrationDate),
                scheduleTracker.firstAlertScheduledFor(openMRSId, TT_VACCINATION).getAlertAsLocalDate());

        final LocalDate dateAfterConception = pregnancyIn12thWeekOfPregnancy.dateOfConception().plusWeeks(3);

        TestCareHistory careHistory = TestCareHistory.withoutHistory(patientId).staffId(staffId)
                .withIPT("1", dateAfterConception).withTT("2", dateAfterConception).withDate(registrationDate);

        mobile.upload(MobileForm.careHistoryForm(), careHistory.forMobile());

        // schedule dates should not change
        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_IPT_VACCINE, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(TT_VACCINATION, registrationDate),
                scheduleTracker.firstAlertScheduledFor(openMRSId, TT_VACCINATION).getAlertAsLocalDate());
    }

    @Test
    public void shouldNotCreateSchedulesIfHistoryIsCapturedBeforeANC() {
        String staffId = staffGenerator.createStaff(browser, homePage);
        String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);
        String openMRSId = openMRSDB.getOpenMRSId(patientId);

        final LocalDate registrationDate = today();
        TestCareHistory careHistory = TestCareHistory.withoutHistory(patientId).staffId(staffId).withDate(registrationDate);
        careHistory.withIPT("1", registrationDate.minusWeeks(5));
        careHistory.withTT("2", registrationDate.minusWeeks(5));

        mobile.upload(MobileForm.careHistoryForm(), careHistory.forMobile());

        assertNull(scheduleTracker.activeEnrollment(openMRSId, ANC_IPT_VACCINE));
        assertNull(scheduleTracker.activeEnrollment(openMRSId, TT_VACCINATION));
    }


    private LocalDate expectedFirstAlertDate(String scheduleName, LocalDate referenceDate) {
        return scheduleTracker.firstAlert(scheduleName, referenceDate);
    }

    private LocalDate expectedFirstAlertDate(String milestoneName, String scheduleName, LocalDate referenceDate) {
        return scheduleTracker.firstAlert(scheduleName, referenceDate, milestoneName);
    }

}
