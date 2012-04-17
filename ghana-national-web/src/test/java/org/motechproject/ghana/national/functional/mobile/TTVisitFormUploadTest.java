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

import java.util.HashMap;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.joda.time.format.DateTimeFormat.forPattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class TTVisitFormUploadTest extends OpenMRSAwareFunctionalTest{

    @Test
    public void shouldUploadTTVisitFormSuccessfully() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);
        final LocalDate visitDate= DateUtil.today();

        final XformHttpClient.XformResponse response = mobile.upload(MobileForm.ttVisitForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("motechId", patientId);
            put("date", visitDate.toString(forPattern("yyyy-MM-dd")));
            put("ttDose","1");
        }});

        assertEquals(1,response.getSuccessCount());

        OpenMRSPatientPage openMRSPatientPage = openMRSBrowser.toOpenMRSPatientPage(openMRSDB.getOpenMRSId(patientId));
        String encounterId = openMRSPatientPage.chooseEncounter("TTVISIT");
        OpenMRSEncounterPage openMRSEncounterPage = openMRSBrowser.toOpenMRSEncounterPage(encounterId);
        openMRSEncounterPage.displaying(asList(
                new OpenMRSObservationVO("TETANUS TOXOID DOSE", "1.0")
        ));
    }
}
