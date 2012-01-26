package org.motechproject.ghana.national.functional.Generator;

import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.framework.Browser;
import org.motechproject.functional.pages.home.HomePage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.stereotype.Component;

@Component
public class StaffGenerator {

    @LoginAsAdmin
    @ApiSession
    public String createStaff(Browser browser, HomePage homePage) {
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("firstName"));
        staffPage.waitForSuccessMessage();
        return staffPage.staffId();
    }
}
