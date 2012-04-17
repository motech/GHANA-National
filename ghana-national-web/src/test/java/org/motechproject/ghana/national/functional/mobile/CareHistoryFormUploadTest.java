package org.motechproject.ghana.national.functional.mobile;

import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.OpenMRSAwareFunctionalTest;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSEncounterPage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSPatientPage;
import org.motechproject.ghana.national.functional.pages.openmrs.vo.OpenMRSObservationVO;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.joda.time.format.DateTimeFormat.forPattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class CareHistoryFormUploadTest extends OpenMRSAwareFunctionalTest {

    @Test
    public void shouldUploadCareHistoryFormWithRelevantANCDetails() {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);
        String openMRSId = openMRSDB.getOpenMRSId(patientId);
        final LocalDate ttDate = DateUtil.newDate(2012, 1, 1);
        final LocalDate iptDate = DateUtil.newDate(2011, 12, 15);
        final LocalDate date = DateUtil.newDate(2012, 1, 15);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.careHistoryForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", "13212");
            put("motechId", patientId);
            put("date", date.toString(forPattern("yyyy-MM-dd")));
            put("addHistory", "IPT_SP,TT");
            put("lastIPT", "1");
            put("lastIPTDate", iptDate.toString(forPattern("yyyy-MM-dd")));
            put("lastTT", "2");
            put("lastTTDate", ttDate.toString(forPattern("yyyy-MM-dd")));
        }});

        assertEquals(1, response.getSuccessCount());

        OpenMRSPatientPage openMRSPatientPage = openMRSBrowser.toOpenMRSPatientPage(openMRSId);
        String encounterId = openMRSPatientPage.chooseEncounter("PATIENTHISTORY");
        OpenMRSEncounterPage openMRSEncounterPage = openMRSBrowser.toOpenMRSEncounterPage(encounterId);
        openMRSEncounterPage.displaying(Arrays.<OpenMRSObservationVO>asList(
                new OpenMRSObservationVO("INTERMITTENT PREVENTATIVE TREATMENT DOSE", "1.0"),
                new OpenMRSObservationVO("TETANUS TOXOID DOSE", "2.0")
        ));
    }

    @Test
    public void shouldUploadCareHistoryFormWithRelevantCWCDetails() {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);
        String openMRSId = openMRSDB.getOpenMRSId(patientId);
        final LocalDate vaccinationDate = DateUtil.newDate(2012, 1, 1);
        final LocalDate date = DateUtil.newDate(2012, 1, 15);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.careHistoryForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", "13212");
            put("motechId", patientId);
            put("date", date.toString(forPattern("yyyy-MM-dd")));
            put("addHistory", "VITA_A,IPTI,BCG,OPV,PENTA,MEASLES,YF");
            put("bcgDate", vaccinationDate.toString(forPattern("yyyy-MM-dd")));
            put("lastOPV", "1");
            put("lastOPVDate", vaccinationDate.toString(forPattern("yyyy-MM-dd")));
            put("lastPenta", "1");
            put("lastPentaDate", vaccinationDate.toString(forPattern("yyyy-MM-dd")));
            put("measlesDate", vaccinationDate.toString(forPattern("yyyy-MM-dd")));
            put("yellowFeverDate", vaccinationDate.toString(forPattern("yyyy-MM-dd")));
            put("lastIPTI", "1");
            put("lastIPTIDate", vaccinationDate.toString(forPattern("yyyy-MM-dd")));
            put("lastVitaminADate", vaccinationDate.toString(forPattern("yyyy-MM-dd")));
        }});

        assertEquals(1, response.getSuccessCount());

        OpenMRSPatientPage openMRSPatientPage = openMRSBrowser.toOpenMRSPatientPage(openMRSId);
        String encounterId = openMRSPatientPage.chooseEncounter("PATIENTHISTORY");
        OpenMRSEncounterPage openMRSEncounterPage = openMRSBrowser.toOpenMRSEncounterPage(encounterId);
        openMRSEncounterPage.displaying(Arrays.<OpenMRSObservationVO>asList(
                new OpenMRSObservationVO("IMMUNIZATIONS ORDERED", "BACILLE CAMILE-GUERIN VACCINATION"),
                new OpenMRSObservationVO("IMMUNIZATIONS ORDERED", "MEASLES VACCINATION"),
                new OpenMRSObservationVO("IMMUNIZATIONS ORDERED", "VITAMIN A"),
                new OpenMRSObservationVO("IMMUNIZATIONS ORDERED", "YELLOW FEVER VACCINATION"),
                new OpenMRSObservationVO("INTERMITTENT PREVENTATIVE TREATMENT INFANTS DOSE", "1.0"),
                new OpenMRSObservationVO("ORAL POLIO VACCINATION DOSE", "1.0"),
                new OpenMRSObservationVO("PENTA VACCINATION DOSE", "1.0")
        ));
    }

}
