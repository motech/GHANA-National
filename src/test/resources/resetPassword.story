Reset password

Meta:
@category indev

Narrative:

As a User I want to reset my password So that I can change my password.

Scenario: Successful Reset Password

Given the admin user logs in with password admin
And the adminDashboard page is displayed
And I follow reset password link
Then I should be on reset password page


