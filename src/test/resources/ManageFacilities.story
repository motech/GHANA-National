Manage Facilities

Meta:
@category smoke

Narrative:

As an Super Admin User I want to manage the facilities in the regio hierarchy so that later I can register patients under a facility.

Scenario: Create a facility

Given the admin user logs in with password admin
And the user follows the link to create a facility
When the user submits a form for a new facility with details
|name|country|region|district|sub-district|phone numbers|
|accra state hospital|ghana|ashanti|||0123456789|

