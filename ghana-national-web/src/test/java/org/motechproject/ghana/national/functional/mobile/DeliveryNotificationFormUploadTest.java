package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class DeliveryNotificationFormUploadTest extends LoggedInUserFunctionalTest {

    @Test
    public void shouldUploadDeliveryStatusNotificationSuccessfully() throws Exception {

        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);

        XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.deliveryNotificationForm() , new HashMap<String, String>() {{
            put("staffId", staffId);
            put("motechId", patientId);
            put("facilityId", facilityId);
            put("datetime", "2010-02-02 12:00:00 PM");
        }});

        assertEquals(1, xformResponse.getSuccessCount());
    }
}
