package org.ghana.national.dao;

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
public class AllSuperAdminsTest {
    @Autowired
    private AllSuperAdmins allSuperAdmins;
    private SuperAdmin admin;

    @Before
    public void setUp() {
        admin = new SuperAdmin("ram");
        allSuperAdmins.add(admin);
    }

    @After
    public void after() {
        allSuperAdmins.remove(admin);
    }

    @Test
    public void shouldLoadAUserBasedOnUsernameAndHashedPassword() {
        String userName = admin.getUsername();
        SuperAdmin superAdmin = allSuperAdmins.findByUserNameAndPassword(userName, "bar");
        assertThat(superAdmin, is(notNullValue()));
        assertThat(superAdmin.getUsername(), is(equalTo(userName)));
    }

    @Test
    public void shouldLoadUserDetailsForAnAuthenticatedUser() {
        UserDetails userDetails = allSuperAdmins.getAuthenticatedUser(admin.getUsername(), "password");
        assertThat(userDetails, is(notNullValue()));
        assertThat(userDetails.getUsername(), is(equalTo(admin.getUsername())));
    }
}
