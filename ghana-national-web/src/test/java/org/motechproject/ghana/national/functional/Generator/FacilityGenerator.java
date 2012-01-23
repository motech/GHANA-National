package org.motechproject.ghana.national.functional.Generator;

import org.motechproject.functional.data.TestFacility;
import org.motechproject.functional.framework.Browser;
import org.motechproject.functional.pages.facility.FacilityPage;
import org.motechproject.functional.pages.home.HomePage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FacilityGenerator {

    @Autowired
    private FacilityService facilityService;
    DataGenerator dataGenerator;

    @LoginAsAdmin
    @ApiSession
    public String createFacilityAndReturnFacilityId(Browser browser, HomePage homePage) {
        dataGenerator = new DataGenerator();
        FacilityPage facilityPage = browser.toFacilityPage(homePage);
        TestFacility facility = TestFacility.with("facility ghana" + dataGenerator.randomString(2));
        facilityPage.save(facility);
        return facilityPage.facilityId();
    }
}
