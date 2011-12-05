package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HomePage {

    private WebDriver driver;

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

        try {
            Thread.sleep(5000);
            driver.findElement(By.id("newfacility")).click();
        } catch (Exception e) {

            System.out.println("Exception " + e.getMessage()); return false;
        }
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


