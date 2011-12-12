package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HomePage {

    @Autowired
    WebDriverProvider webDriverProvider;

    @Autowired
    JavascriptExecutor javascriptExecutor;

    private WebDriver driver;
    private org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HomePage.class);

    @Autowired
    public HomePage(WebDriverProvider driverProvider) {
        this.driver = driverProvider.getWebDriver();
    }


    WebElement CreateFacilityLink, CreatePatientLink, ResetPassLink, CreateStaffLink, LogoutLink;


    public void Logout() {
        LogoutLink = driver.findElement(By.linkText("Logout"));
        LogoutLink.click();
    }

    public boolean OpenCreateFacilityPage() {
        WebElement FacilityParentLink = driver.findElement(By.linkText("Facility"));
        FacilityParentLink.click();

        WebElement newFacilityLink = javascriptExecutor.getElementById("newfacility", driver);
        newFacilityLink.click();

        return true;
    }

    public boolean OpenCreateStaffPage() {
        CreateStaffLink = driver.findElement(By.id("newstaff"));
        try {
            CreateStaffLink.click();
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public boolean OpenCreatePatientPage() {
        CreatePatientLink = driver.findElement(By.id("newpatient"));
        try {

            CreatePatientLink.click();
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;

    }
}


