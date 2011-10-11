Reset password

Meta:
@category main

Narrative:

As a User I want to reset my password So that I can change my password.

Scenario: Successful Reset Password

Given the admin user logs in with password P@ssw0rd
Then the adminDashboard page is displayed
And I follow Reset Password link
Then the resetPassword page is displayed


