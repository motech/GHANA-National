package org.ghana.national.steps;

import org.ghana.national.pages.PageFactory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;

public class LoginSteps {
    private PageFactory pageFactory;

    public LoginSteps(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
    }

    @Given("the $user user logs in with password $password")
    public void userLogsIn(String user, String password) {

    }

    @Then("the $page page should be displayed")
    public void displaysPage(String page) {

    }

    @Then("the $page page should be displayed with $error")
    public void displaysPage(String page, String error) {

    }

}
