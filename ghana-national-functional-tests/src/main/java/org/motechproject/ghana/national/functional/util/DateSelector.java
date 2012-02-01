package org.motechproject.ghana.national.functional.util;

import org.joda.time.LocalDate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DateSelector {
    public void select(WebElement dateElement, LocalDate date, WebDriver driver) {
        ElementPoller elementPoller = new ElementPoller();
        dateElement.findElement(By.className("ui-datepicker-trigger")).click();
        elementPoller.waitForElementClassName("ui-datepicker-year", driver);
        new Select(driver.findElement(By.className("ui-datepicker-year"))).selectByValue(Integer.toString(date.getYear()));
        new Select(driver.findElement(By.className("ui-datepicker-month"))).selectByValue(Integer.toString(date.getMonthOfYear() - 1));
        driver.findElement(By.className("ui-datepicker-calendar")).findElement(By.xpath("//a[text()='" + date.getDayOfMonth() + "']")).click();
    }
}
