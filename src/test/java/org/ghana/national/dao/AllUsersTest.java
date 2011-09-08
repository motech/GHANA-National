package org.ghana.national.dao;

import org.ghana.national.domain.CallCenterAdmin;
import org.ghana.national.domain.FacilityAdmin;
import org.ghana.national.domain.SuperAdmin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/applicationContext.xml"})
public class AllUsersTest {
    @Autowired
    private AllSuperAdmins allSuperAdmins;
    @Autowired
    private AllCallCenterAdmins allCallCenterAdmins;
    @Autowired
    private AllFacilityAdmins allFacilityAdmins;

    private SuperAdmin superAdmin;
    private CallCenterAdmin callCenterAdmin;
    private FacilityAdmin facilityAdmin;

    @Before
    public void setUp() {
        superAdmin = new SuperAdmin("admin");
        callCenterAdmin = new CallCenterAdmin("call");
        facilityAdmin = new FacilityAdmin("facility");
        allSuperAdmins.add(superAdmin);
        allCallCenterAdmins.add(callCenterAdmin);
        allFacilityAdmins.add(facilityAdmin);
    }

    @After
    public void after() {
        allSuperAdmins.remove(superAdmin);
        allCallCenterAdmins.remove(callCenterAdmin);
        allFacilityAdmins.remove(facilityAdmin);
    }

    @Test
    public void shouldLoadAUserBasedOnUsernameAndHashedPassword() {
        assertUser(superAdmin.getUsername(), allSuperAdmins.findByUserNameAndPassword(superAdmin.getUsername(), "bar"));
        assertUser(callCenterAdmin.getUsername(), allCallCenterAdmins.findByUserNameAndPassword(callCenterAdmin.getUsername(), "bar"));
        assertUser(facilityAdmin.getUsername(), allFacilityAdmins.findByUserNameAndPassword(facilityAdmin.getUsername(), "bar"));
    }

    private void assertUser(String userName, UserDetails user) {
        assertThat(user, is(notNullValue()));
        assertThat(user.getUsername(), is(equalTo(userName)));
    }

    @Test
    public void shouldLoadUserDetailsForAnAuthenticatedUser() {
        UserDetails superAdminUserDetails = allSuperAdmins.getAuthenticatedUser(superAdmin.getUsername(), "password");
        assertUser(superAdminUserDetails.getUsername(), superAdminUserDetails);

        UserDetails callCenterAdminDetails = allCallCenterAdmins.getAuthenticatedUser(callCenterAdmin.getUsername(), "password");
        assertUser(callCenterAdminDetails.getUsername(), callCenterAdminDetails);

        UserDetails facilityAdminDetails = allFacilityAdmins.getAuthenticatedUser(facilityAdmin.getUsername(), "password");
        assertUser(facilityAdminDetails.getUsername(), facilityAdminDetails);
    }
}
