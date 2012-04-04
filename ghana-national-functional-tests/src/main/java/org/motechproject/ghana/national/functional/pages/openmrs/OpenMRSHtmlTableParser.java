package org.motechproject.ghana.national.functional.pages.openmrs;

import org.motechproject.ghana.national.functional.util.HtmlTableParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

@Component
public class OpenMRSHtmlTableParser extends HtmlTableParser {
    @Override
    protected List<String> getHeading(WebElement table, String tableId) {
        List<WebElement> rows = table.findElements(By.xpath("id('" + tableId + "')/thead/tr"));
        return collect(rows.get(0).findElements(By.xpath("th")), on(WebElement.class).getText());
    }
}
