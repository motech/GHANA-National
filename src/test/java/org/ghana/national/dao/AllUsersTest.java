package org.ghana.national.dao;

import org.ghana.national.domain.CallCenterAdmin;
import org.ghana.national.domain.FacilityAdmin;
import org.ghana.national.domain.SuperAdmin;
import org.ghana.national.domain.User;
import org.jasypt.util.password.StrongPasswordEncryptor;
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
import static org.hamcrest.core.IsNull.nullValue;
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
        superAdmin = new SuperAdmin("admin", "admin");
        callCenterAdmin = new CallCenterAdmin("call", "call");
        facilityAdmin = new FacilityAdmin("facility", "facility");
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
    public void shouldEncryptPasswordWhileAddingAUser() {
        assertPasswordIsEncrypted(allCallCenterAdmins.findByUsername(callCenterAdmin.getUsername()), callCenterAdmin.getPassword());
        assertPasswordIsEncrypted(allFacilityAdmins.findByUsername(facilityAdmin.getUsername()), facilityAdmin.getPassword());
        assertPasswordIsEncrypted(allSuperAdmins.findByUsername(superAdmin.getUsername()), superAdmin.getPassword());
    }

    @Test
    public void shouldLoadAUserBasedOnUsernameAndHashedPassword() {
        assertUser(callCenterAdmin.getUsername(), allCallCenterAdmins.findByUsername(callCenterAdmin.getUsername()));
        assertThat(allSuperAdmins.findByUsername(callCenterAdmin.getUsername()), is(nullValue()));
        assertThat(allFacilityAdmins.findByUsername(callCenterAdmin.getUsername()), is(nullValue()));

        assertUser(facilityAdmin.getUsername(), allFacilityAdmins.findByUsername(facilityAdmin.getUsername()));
        assertThat(allSuperAdmins.findByUsername(facilityAdmin.getUsername()), is(nullValue()));
        assertThat(allCallCenterAdmins.findByUsername(facilityAdmin.getUsername()), is(nullValue()));

        assertUser(superAdmin.getUsername(), allSuperAdmins.findByUsername(superAdmin.getUsername()));
        assertThat(allCallCenterAdmins.findByUsername(superAdmin.getUsername()), is(nullValue()));
        assertThat(allFacilityAdmins.findByUsername(superAdmin.getUsername()), is(nullValue()));

    }

    @Test
    public void shouldLoadUserDetailsForAnAuthenticatedUser() {
        UserDetails superAdminUserDetails = allSuperAdmins.getAuthenticatedUser(superAdmin.getUsername(), superAdmin.getPassword());
        assertUser(superAdminUserDetails.getUsername(), superAdminUserDetails);

        UserDetails callCenterAdminDetails = allCallCenterAdmins.getAuthenticatedUser(callCenterAdmin.getUsername(), callCenterAdmin.getPassword());
        assertUser(callCenterAdminDetails.getUsername(), callCenterAdminDetails);

        UserDetails facilityAdminDetails = allFacilityAdmins.getAuthenticatedUser(facilityAdmin.getUsername(), facilityAdmin.getPassword());
        assertUser(facilityAdminDetails.getUsername(), facilityAdminDetails);
    }

    @Test
    public void shouldMatchTheProvidedPasswordForUserAuthentication() {
        assertThat(allSuperAdmins.getAuthenticatedUser(superAdmin.getUsername(), "bad password"), is(nullValue()));
        assertThat(allFacilityAdmins.getAuthenticatedUser(facilityAdmin.getUsername(), "bad password"), is(nullValue()));
        assertThat(allCallCenterAdmins.getAuthenticatedUser(callCenterAdmin.getUsername(), "bad password"), is(nullValue()));
    }

    private void assertUser(String userName, UserDetails user) {
        assertThat(user, is(notNullValue()));
        assertThat(user.getUsername(), is(equalTo(userName)));
    }

    private void assertPasswordIsEncrypted(User user, String plainPassword) {
        assertThat(user.getPassword(), is(nullValue()));
        assertThat(user.getDigestedPassword(), is(notNullValue()));
        assertThat(new StrongPasswordEncryptor().checkPassword(plainPassword, user.getDigestedPassword()), is(true));
    }
}
