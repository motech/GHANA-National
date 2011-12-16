package org.motechproject.ghana.national.functional;

import org.junit.runner.RunWith;
import org.openqa.selenium.support.PageFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class UpdateFacilityTest extends CreateFacilityTest {

    @BeforeMethod
    public void setUp() {
        super.setUp();
    }

    @Test   (enabled = false)
    public void updateFacilityWithValidValues() {
        createFacilityWithValidValues();
        PageFactory.initElements(driver, createFacilityPage);
        createFacilityPage.SetFacilityName("Updated Facility");
        Assert.assertTrue(createFacilityPage.UpdateFacilityDetails());
    }

    @AfterSuite
    public void closeall() {
        driver.close();
    }
}
