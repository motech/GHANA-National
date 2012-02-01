package org.motechproject.ghana.national.functional.util;

import org.motechproject.ghana.national.functional.data.OpenMRSTestUser;
import org.motechproject.ghana.national.functional.framework.Browser;
import org.motechproject.ghana.national.functional.pages.openmrs.MotechIdGeneratorPage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSLoginPage;

public class MotechIdGenerator {
    private Browser browser;

    public MotechIdGenerator(Browser browser) {
        this.browser = browser;
    }

    public String generatePrePrintedPatientId() {
        OpenMRSLoginPage loginPage = browser.toOpenMRSLogin();
        loginPage.login(OpenMRSTestUser.admin());
        browser.openMRSHomePage();
        MotechIdGeneratorPage motechIdGeneratorPage = browser.toMotechIdGenerator("/openmrs/module/idgen/viewIdentifierSource.form?source=1");
        motechIdGeneratorPage.generate(1);
        return null;
    }
}
