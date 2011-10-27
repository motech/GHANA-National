package org.motechproject.ghana.national.steps;

import org.jbehave.core.annotations.Then;
import org.motechproject.ghana.national.pages.AdminDashboardPage;
import org.motechproject.ghana.national.pages.PageFactory;

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
