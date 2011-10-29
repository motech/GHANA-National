package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
        assertEquals(1,all.size());
        assertEquals("test",all.get(0).name());
    }

    @After
    public void tearDown() {
        List<UserType> all = allUserTypes.getAll();
        for (UserType userType : all)
            allUserTypes.remove(userType);
    }
}
