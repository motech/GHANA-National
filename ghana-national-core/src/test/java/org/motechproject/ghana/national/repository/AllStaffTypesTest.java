package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.StaffType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class AllStaffTypesTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private AllStaffTypes allStaffTypes;

    @Test
    public void shouldSaveAUserType() {
        StaffType staffType = new StaffType("test", "desc");

        allStaffTypes.add(staffType);

        List<StaffType> all = allStaffTypes.getAll();
        assertEquals(1, all.size());
        assertEquals("test", all.get(0).name());
    }

    @After
    public void tearDown() {
        List<StaffType> all = allStaffTypes.getAll();
        for (StaffType staffType : all)
            allStaffTypes.remove(staffType);
    }
}
