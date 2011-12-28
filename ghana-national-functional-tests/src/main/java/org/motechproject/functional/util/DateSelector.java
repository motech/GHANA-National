package org.motechproject.functional.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class DateSelector {
    @Autowired
    private ElementPoller elementPoller;

    public void select(WebElement dateElement, Calendar date, WebDriver driver){
        dateElement.findElement(By.className("ui-datepicker-trigger")).click();
        elementPoller.waitForElementClassName("ui-datepicker-year", driver);
        new Select(driver.findElement(By.className("ui-datepicker-year"))).selectByValue(Integer.toString(date.get(Calendar.YEAR)));
        new Select(driver.findElement(By.className("ui-datepicker-month"))).selectByValue(Integer.toString(date.get(Calendar.MONTH) - 1));
        driver.findElement(By.className("ui-datepicker-calendar")).findElement(By.xpath("//a[text()='" + Integer.toString(date.get(Calendar.DATE)) +"']")).click();
    }
}
