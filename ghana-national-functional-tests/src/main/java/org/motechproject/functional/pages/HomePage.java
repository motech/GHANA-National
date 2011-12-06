package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
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


        try {
            Actions movetoMenu = new Actions(driver);
            movetoMenu.moveToElement(FacilityParentLink).build().perform();
            movetoMenu.click();
            (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    //return d.findElement(By.id("newFacility")).isDisplayed();
                    return d.findElement(By.id("newfacility")).isDisplayed();
                }
            });

            driver.findElement(By.id("newfacility")).click();
        } catch(Exception e) {

            System.out.println("Exception " + e.getMessage());
            return false;
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


