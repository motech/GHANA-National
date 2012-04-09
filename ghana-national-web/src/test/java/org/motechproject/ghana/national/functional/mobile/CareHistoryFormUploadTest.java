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

import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class CareHistoryFormUploadTest extends LoggedInUserFunctionalTest {

    @Autowired
    ScheduleTracker scheduleTracker;
    @Autowired
    private OpenMRSDB openMRSDB;

    @Test
    public void shouldNotCreateSchedulesWhileHistoryFormUploadIfThereAreActiveSchedules() {
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

        TestCareHistory careHistory = TestCareHistory.withoutHistory(patientId);
        careHistory.withIPT("1", dateAfterConception);
        careHistory.withTT("2", dateAfterConception);

        mobile.upload(MobileForm.careHistoryForm(),careHistory.forMobile());

        // schedule dates should not change
        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(ANC_IPT_VACCINE, pregnancyIn12thWeekOfPregnancy.dateOfConception()),
                scheduleTracker.firstAlertScheduledFor(openMRSId, ANC_IPT_VACCINE).getAlertAsLocalDate());

        ScheduleHelper.assertAlertDate(expectedFirstAlertDate(TT_VACCINATION, registrationDate),
                scheduleTracker.firstAlertScheduledFor(openMRSId, TT_VACCINATION).getAlertAsLocalDate());
    }


    private LocalDate expectedFirstAlertDate(String scheduleName, LocalDate referenceDate) {
        return scheduleTracker.firstAlert(scheduleName, referenceDate);
    }

}
