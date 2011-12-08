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

    @Autowired
    WebDriverProvider webDriverProvider;

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
        webDriverProvider.WaitForElement_ID("newfacility");
        driver.findElement(By.id("newfacility")).click();
        return webDriverProvider.WaitForElement_ID("name");
//        try {
//            Actions movetoMenu = new Actions(driver);
//            movetoMenu.moveToElement(FacilityParentLink).build().perform();
//            movetoMenu.click();
//
//            webDriverProvider.WaitForElement_ID("newfacility");
//            driver.findElement(By.id("newfacility")).click();
//
//            if (webDriverProvider.WaitForElement_ID("name")) {
//                return true;
//            }
//        } catch (Exception e) {
//            log.debug("Exception from HomePage" + e.getMessage());
//            System.out.println("Exception from HomePage" + e.getMessage());
//            return false;
//        }
        //return true;
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


