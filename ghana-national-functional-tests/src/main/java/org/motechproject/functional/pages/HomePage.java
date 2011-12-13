package org.motechproject.functional.pages;

import org.apache.commons.io.FileUtils;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.JavascriptExecutor;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


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
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot,new File("blah.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FacilityParentLink.click();
        WebElement temp= myJsExecutor.getElementById("newfacility",driver);
        temp.click();
////        // The below line of code does not work in Linux env so commenting it out and
////        // added the line above this comment to pass the build in hudson
//        myJsExecutor.clickOnLink("newfacility", driver);
        webDriverProvider.WaitForElement_ID("submitFacility");
    }


    public void OpenCreatePatientPage() {
        WebElement PatientParentLink = driver.findElement(By.linkText("Patient"));
        PatientParentLink.click();

        WebElement temp = myJsExecutor.getElementById("newpatient", driver);
        temp.click();
//        myJsExecutor.clickOnLink("newpatient", driver);
        webDriverProvider.WaitForElement_ID("submitNewPatient");
    }
}


