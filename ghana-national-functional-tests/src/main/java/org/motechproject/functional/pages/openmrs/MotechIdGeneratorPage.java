package org.motechproject.functional.pages.openmrs;

import org.motechproject.functional.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MotechIdGeneratorPage extends BasePage<MotechIdGeneratorPage> {
    @FindBy(name = "numberToGenerate")
    private WebElement numberToGenerate;

    @FindBy(xpath = "//input[6]")
    private WebElement submit;

    public MotechIdGeneratorPage(WebDriver driver) {
        super(driver);
        elementPoller.waitFor(driver, By.name("numberToGenerate"));
        PageFactory.initElements(driver, this);
    }

    public void generate(int number) {
        enter(numberToGenerate, Integer.toString(number));
        submit.click();
    }
}
