package org.motechproject.ghana.national.functional.Generator;

import org.motechproject.ghana.national.functional.data.TestFacility;
import org.motechproject.ghana.national.functional.framework.Browser;
import org.motechproject.ghana.national.functional.pages.facility.FacilityPage;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;

public class FacilityGenerator {

    @LoginAsAdmin
    @ApiSession
    public String createFacility(Browser browser, HomePage homePage) {
        DataGenerator dataGenerator = new DataGenerator();
        FacilityPage facilityPage = browser.toFacilityPage(homePage);
        TestFacility facility = TestFacility.with("facility ghana" + dataGenerator.randomString(2));
        facilityPage.save(facility);
        facilityPage.waitForSuccessMessage();
        return facilityPage.facilityId();
    }
}
