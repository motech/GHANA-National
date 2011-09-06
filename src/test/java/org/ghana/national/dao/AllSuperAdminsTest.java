package org.ghana.national.dao;

import org.ghana.national.domain.SuperAdmin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/applicationContext.xml"})
public class AllSuperAdminsTest {
    @Autowired
    private AllSuperAdmins allSuperAdmins;

    @Test
    public void shouldLoadAUserBasedOnUsernameAndHashedPassword() {
        SuperAdmin superAdmin = allSuperAdmins.findByUserNameAndPassword("foo", "bar");
        assertThat(superAdmin, is(notNullValue()));
    }
}
