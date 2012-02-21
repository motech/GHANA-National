package org.motechproject.ghana.national.functional.mobile;


import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.joda.time.format.DateTimeFormat.forPattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class PregnancyTerminationMobileUploadTest extends LoggedInUserFunctionalTest {


    @Test
    public void shouldUploadPregnancyTerminationFormSuccessfully() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String motechId=patientGenerator.createPatientWithStaff(browser,homePage,staffId);
        final LocalDate terminationDate= DateUtil.today();

        final XformHttpClient.XformResponse response = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XformHttpClient.XFormParser.parse("pregnancy-termination-template.xml", new HashMap<String, String>() {{

            put("staffId", staffId);
            put("facilityId", facilityId);
            put("motechId", motechId);
            put("date", terminationDate.toString(forPattern("yyyy-MM-dd")));
            put("terminationType","1");
            put("procedure","2");
            put("complications","1,2");
            put("maternalDeath","N");
            put("postAbortiobFPCounseled","Y");
            put("postAbortionFPAccepted","Y");
            put("referred", "Y");
            put("comments", "blah blah");

        }}));

        assertEquals(1,response.getSuccessCount());
    }

}
