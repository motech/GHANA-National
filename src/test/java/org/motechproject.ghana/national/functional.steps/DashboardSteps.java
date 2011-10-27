package org.motechproject.ghana.national.functional.steps;

import org.jbehave.core.annotations.Then;
import org.motechproject.ghana.national.functional.pages.AdminDashboardPage;
import org.motechproject.ghana.national.functional.pages.PageFactory;

public class DashboardSteps {
    private PageFactory pageFactory;
    private AdminDashboardPage adminDashBoard;

    public DashboardSteps(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
        this.adminDashBoard = pageFactory.getAdminDashBoard();
    }

    @Then("I follow $linkName link")
    public void followLink(String linkName) {
        adminDashBoard.follow(linkName);
    }
}
