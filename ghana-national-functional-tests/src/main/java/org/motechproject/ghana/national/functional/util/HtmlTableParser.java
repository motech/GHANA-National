package org.motechproject.ghana.national.functional.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

@Component
public class HtmlTableParser {
    public boolean hasRow(WebDriver driver, String tableId, Map<String, String> data) {
        return (getRow(driver, tableId, data) != null);
    }

    public void clickEditLink(WebDriver driver, String tableId, Map<String, String> data) {
        getRow(driver, tableId, data).findElement(By.xpath("//a[@title='Edit']")).click();
    }

    public WebElement getRow(WebDriver driver, String tableId, Map<String, String> data) {
        WebElement table = driver.findElement(By.id(tableId));
        List<WebElement> rows = table.findElements(By.xpath("id('" + tableId + "')/tbody/tr"));
        List<String> headings = getHeading(table, tableId);
        for (WebElement row : rows) {
            List<WebElement> cols = new ArrayList<WebElement>();
            if(!StringUtils.isEmpty(row.getText())) {
                cols = row.findElements(By.xpath("td"));
            }
            List<String> rowText = collect(cols, on(WebElement.class).getText());
            if (matchRow(data, headings, rowText)) {
                return row;
            }
        }
        return null;
    }

    protected List<String> getHeading(WebElement table, String tableId) {
        List<WebElement> rows = table.findElements(By.xpath("id('" + tableId + "')/tbody/tr"));
        return collect(rows.get(0).findElements(By.xpath("th")), on(WebElement.class).getText());
    }

    private boolean matchRow(Map<String, String> data, List<String> headings, List<String> rowText) {
        if (data.keySet().size() > rowText.size()) {
            return false;
        }
        boolean matches = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String columnText = rowText.get(headings.indexOf(entry.getKey()));
            if (!columnText.equals(blankIfNull(entry.getValue()))) {
                matches = false;
                break;
            }
        }
        return matches;
    }

    private String blankIfNull(String value) {
        return value == null ? "" : value;
    }
}
