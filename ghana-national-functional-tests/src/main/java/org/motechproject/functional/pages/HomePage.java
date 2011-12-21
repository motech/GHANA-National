package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class HomePage {
    @Value("#{functionalTestProperties['host']}")
    private String host;

    @Value("#{functionalTestProperties['port']}")
    private String port;

    @Autowired
    WebDriverProvider webDriverProvider;

    private WebDriver driver;
    private org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HomePage.class);

    @Autowired
    public HomePage(WebDriverProvider driverProvider) {
        this.driver = driverProvider.getWebDriver();
    }

    @Autowired
    public JavascriptExecutor myJsExecutor;

    WebElement LogoutLink;


    public void Logout() {
        LogoutLink = driver.findElement(By.linkText("Logout"));
        LogoutLink.click();
    }

    public void OpenCreateFacilityPage() {
        WebElement FacilityParentLink = driver.findElement(By.linkText("Facility"));


        if (System.getProperty("os.name").contains("Wind")) {
            myJsExecutor.clickOnLink("newfacility", driver);
            System.out.println("OS NAME" + System.getProperty("os.name"));
        } else {
            //System.out.println("OS NAME" + System.getProperty("os.name"));
            WebElement temp = myJsExecutor.getElementById("newfacility", driver);

            FacilityParentLink.click();
            webDriverProvider.waitForElement_ID("newfacility");
            temp.click();
        }
        webDriverProvider.waitForElement_ID("submitFacility");
    }

    public void OpenCreateStaffPage() {
            WebElement StaffParentLink = driver.findElement(By.linkText("Staff"));
        if (System.getProperty("os.name").contains("Wind")) {
            myJsExecutor.clickOnLink("newstaff", driver);
        } else {
            WebElement temp = myJsExecutor.getElementById("newstaff", driver);

            StaffParentLink.click();
            webDriverProvider.waitForElement_ID("newstaff");
            temp.click();
        }
        webDriverProvider.waitForElement_ID("submitNewUser");
    }


    public void OpenCreatePatientPage() {
        WebElement PatientParentLink = driver.findElement(By.linkText("Patient"));
        if (System.getProperty("os.name").contains("Wind")) {
            myJsExecutor.clickOnLink("newpatient", driver);
        } else {
            WebElement temp = myJsExecutor.getElementById("newpatient", driver);

            PatientParentLink.click();
            webDriverProvider.waitForElement_ID("newpatient");
            temp.click();
        }
        webDriverProvider.waitForElement_ID("submitNewPatient");
    }
}


