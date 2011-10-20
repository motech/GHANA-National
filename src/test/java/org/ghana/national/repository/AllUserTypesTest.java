package org.ghana.national.repository;

import org.ghana.national.domain.UserType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext.xml"})
public class AllUserTypesTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private AllUserTypes allUserTypes;

    @Test
    public void shouldSaveAUserType() {
        UserType userType = new UserType("test", "desc");

        allUserTypes.add(userType);
        List<UserType> all = allUserTypes.getAll();
        assertTrue(all.contains(userType));
    }

    @After
    public void tearDown() {
        List<UserType> all = allUserTypes.getAll();
        for (UserType userType : all)
            allUserTypes.remove(userType);
    }
}
