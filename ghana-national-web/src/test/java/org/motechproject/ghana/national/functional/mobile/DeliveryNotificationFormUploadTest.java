package org.motechproject.ghana.national.functional.mobile;

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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class DeliveryNotificationFormUploadTest extends OpenMRSAwareFunctionalTest{

    @Test
    public void shouldUploadDeliveryStatusNotificationSuccessfully() throws Exception {

        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String patientId = patientGenerator.createPatient(browser, homePage, staffId);

        XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.deliveryNotificationForm() , new HashMap<String, String>() {{
            put("staffId", staffId);
            put("motechId", patientId);
            put("facilityId", facilityId);
            put("datetime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(DateUtil.today().minusDays(2).toDate()));
        }});

        assertEquals(1, xformResponse.getSuccessCount());

        OpenMRSPatientPage openMRSPatientPage = openMRSBrowser.toOpenMRSPatientPage(openMRSDB.getOpenMRSId(patientId));
        String encounterId = openMRSPatientPage.chooseEncounter("PREGDELNOTIFYVISIT");
        OpenMRSEncounterPage openMRSEncounterPage = openMRSBrowser.toOpenMRSEncounterPage(encounterId);

        openMRSEncounterPage.displaying(Collections.<OpenMRSObservationVO>emptyList());

    }
}
