package org.ghana.national.steps;

import org.ghana.national.pages.AdminDashboardPage;
import org.ghana.national.pages.PageFactory;
import org.jbehave.core.annotations.Then;

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
