package org.motechproject.ghana.national.functional.steps;

import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.web.selenium.PerStoriesWebDriverSteps;
import org.jbehave.web.selenium.WebDriverProvider;
import org.motechproject.ghana.national.functional.pages.PageFactory;

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
