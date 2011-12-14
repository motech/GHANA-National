package org.motechproject.ghana.national.functional;

import org.junit.runner.RunWith;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.CreateFacilityPage;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class UpdateFacilityTest extends CreateFacilityTest{

    @BeforeMethod
    public void setUp() {
        super.setUp();
    }

    @Test
    public void updateFacilityWithValidValues() {
        createFacilityWithValidValues();
        PageFactory.initElements(driver, createFacilityPage);
        createFacilityPage.SetFacilityName("Updated Facility");
        createFacilityPage.SubmitDetails();
        String srcPage = driver.getPageSource();
        Assert.assertTrue(srcPage.contains("Facility edited successfully"));
    }


}
