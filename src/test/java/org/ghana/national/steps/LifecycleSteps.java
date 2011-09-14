package org.ghana.national.steps;

import org.ghana.national.pages.PageFactory;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.web.selenium.PerStoriesWebDriverSteps;
import org.jbehave.web.selenium.WebDriverProvider;

public class LifecycleSteps extends PerStoriesWebDriverSteps {

    private PageFactory pageFactory;

    public LifecycleSteps(WebDriverProvider webDriverProvider, PageFactory pageFactory) {
        super(webDriverProvider);
        this.pageFactory = pageFactory;
    }

    @BeforeScenario
    public void logout() {
        pageFactory.getLogin().logout();
    }

}
