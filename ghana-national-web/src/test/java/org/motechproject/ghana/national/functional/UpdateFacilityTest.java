package org.motechproject.ghana.national.functional;

import org.junit.runner.RunWith;
import org.openqa.selenium.By;
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

    @Test
    public void updateFacilityWithValidValues() {
        createFacilityWithValidValues();
        PageFactory.initElements(driver, createFacilityPage);
        createFacilityPage.withFacilityName("Updated Facility").withRegionName("Ashanti");
        Assert.assertTrue(createFacilityPage.updateFacilityDetails());
        Assert.assertFalse(driver.findElement(By.id("districts")).isDisplayed());
        Assert.assertFalse(driver.findElement(By.id("sub-districts")).isDisplayed());
    }

    @AfterSuite
    public void closeall() {
        driver.quit();
    }
}


