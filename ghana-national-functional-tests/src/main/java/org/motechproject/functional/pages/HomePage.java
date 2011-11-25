package org.motechproject.functional.pages;

import org.motechproject.functional.base.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class HomePage {
    // get a driver instance

    WebDriver driver = MyDriver.getDriverInstance();

    // Define home page links / elements

    WebElement CreateFacilityLink, CreatePatientLink, ResetPassLink, CreateStaffLink, LogoutLink;

    public void Logout() {
        LogoutLink = driver.findElement(By.linkText("Logout"));
        LogoutLink.click();
    } // end Logout

    /* @isVefifyHomePageLinks()
    Verify basic structure of home page by checking if links to required pages are present



    public boolean isVerifyHomePageLinks() {
        boolean VerifySuccess = true;
        try {

            CreateFacilityLink = driver.findElement(By.id("newfacility"));
            //.linkText("Create Facility"));
            CreatePatientLink = driver.findElement(By.id(("newpatient")));
            ResetPassLink = driver.findElement(By.linkText("Reset Password"));
            CreateStaffLink = driver.findElement(By.id("newstaff"));
            LogoutLink = driver.findElement(By.linkText("Logout"));

        } catch (NoSuchElementException e) {
            VerifySuccess = false;
        } catch (Exception e) {
            VerifySuccess = false;
            System.out.println("Message" + e.getMessage());
        }
        return VerifySuccess;
    }

*/
    public boolean OpenCreateFacilityPage() {
        CreateFacilityLink = driver.findElement(By.id("newfacility"));
        try {
            WebElement FacilityParentLink = driver.findElement(By.linkText("Facility"));
            FacilityParentLink.click();
            Actions builder = new Actions(driver);
            builder.moveToElement(FacilityParentLink).build().perform();
            Thread.sleep(500);
            driver.findElement(By.id("newfacility")).click();
            //builder.click(CreateFacilityLink).build().perform();
            //CreateFacilityLink.click();

            System.out.print("Clicked on Facility Create Link");
            //builder.contextClick(CreateFacilityLink);

        } catch (Exception e) {
            System.out.println("Exception Thrown ::::::" + e.getMessage());
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


} // end HomePage

/*public boolean clickLink(String LinkText) {

        try {
            WebElement Link = driver.findElement(By.linkText(LinkText));
            Link.click();
            return true;
        } catch (NoSuchElementException e) {
            System.out.println("Exception when clicking link " + LinkText + " " + e.getMessage());
            return false;
        }
*//*

    }*/


