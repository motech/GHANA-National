package org.motechproject.ghana.national.functional;

import org.motechproject.ghana.national.functional.framework.Browser;
import org.motechproject.ghana.national.functional.framework.Mobile;
import org.motechproject.ghana.national.functional.util.ScreenShotCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public abstract class FunctionalTest extends AbstractTestNGSpringContextTests {
    @Autowired
    protected Browser browser;

    @Autowired
    protected Mobile mobile;

    @BeforeMethod
    public void baseTestSetup(Method method) {
        ScreenShotCaptor.setupFor(method);
    }

    @AfterMethod
    public void baseTestTeardown(ITestResult testResult) {
        if (!testResult.isSuccess())
            browser.captureScreenShot();
    }

    @AfterSuite
    public void closeall() {
        browser.quit();
    }
}
