package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginPage {
    private WebDriver driver;

    @Autowired
    public LoginPage(WebDriverProvider driverProvider) {
        this.driver = driverProvider.getWebDriver();
    }

    public boolean LoginAs(String UserName, String Password) {
        String src;
        WebElement uName = driver.findElement(By.name("j_username"));
        uName.sendKeys(UserName);
        WebElement uPass = driver.findElement(By.name("j_password"));
        uPass.sendKeys(Password);
        WebElement BtnLogin = driver.findElement(By.xpath("//input[3]"));

        BtnLogin.click();

        src = driver.getPageSource();
        if (src.contains("j_spring_security_check"))
            return false;
        else
            return true;
    }

}
