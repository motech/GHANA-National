package org.ghana.national.steps;

import org.ghana.national.pages.AdminDashboardPage;
import org.ghana.national.pages.LoginPage;
import org.ghana.national.pages.PageFactory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LoginSteps {
    private PageFactory pageFactory;
    private LoginPage login;
    private AdminDashboardPage adminDashBoard;

    public LoginSteps(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
        this.adminDashBoard = pageFactory.getAdminDashBoard();
        this.login = pageFactory.getLogin();
    }

    @Given("the $user user logs in with password $password")
    public void userLogsIn(String user, String password) {
        adminDashBoard.go();
        assertThat(login.isCurrent(), is(true));
        login.login(user, password);
    }

    @Then("the $page page should be displayed")
    public void displaysPage(String page) {
        assertThat(pageFactory.getPage(page).isCurrent(), is(true));
    }


    @Then("the $page page should be displayed with $error")
    public void displaysPage(String page, String error) {
        displaysPage(page);
    }
}
