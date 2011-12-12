package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.JavascriptExecutor;
import org.openqa.selenium.By;
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

    @Autowired
    public JavascriptExecutor myJsExecutor;

    WebElement LogoutLink;


    public void Logout() {
        LogoutLink = driver.findElement(By.linkText("Logout"));
        LogoutLink.click();
    }

    public void OpenCreateFacilityPage() {
        WebElement FacilityParentLink = driver.findElement(By.linkText("Facility"));
        FacilityParentLink.click();
        myJsExecutor.getElementById("newfacility",driver).click();
        // The below line of code does not work in Linux env so commenting it out and
        // added the line above this comment to pass the build in hudson
        //myJsExecutor.clickOnLink("newfacility", driver);
    }


    public void OpenCreatePatientPage() {
        WebElement PatientParentLink = driver.findElement(By.linkText("Patient"));
        PatientParentLink.click();
        myJsExecutor.getElementById("newpatient",driver).click();
        //myJsExecutor.clickOnLink("newpatient", driver);
    }
}


