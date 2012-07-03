package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.StaffType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AllStaffTypesTest extends BaseIntegrationTest {
    @Autowired
    private AllStaffTypes allStaffTypes;

    @Test
    public void shouldSaveAUserType() {
        StaffType staffType = new StaffType("test", "desc");

        allStaffTypes.add(staffType);

        List<StaffType> all = allStaffTypes.getAll();
        assertEquals(1, all.size());
        assertEquals("test", all.get(0).getName());
    }

    @Test
    public void shouldUpdateAUserType() {
        StaffType staffType = new StaffType("test", "desc");

        allStaffTypes.add(staffType);
        StaffType staffType2 = new StaffType("test", "desc2");
        allStaffTypes.addOrReplace(staffType2);

        List<StaffType> all = allStaffTypes.getAll();
        assertEquals(1, all.size());
        assertEquals("test", all.get(0).getName());
        assertEquals("desc2", all.get(0).getDescription());
    }

    @After
    public void tearDown() {
        List<StaffType> all = allStaffTypes.getAll();
        for (StaffType staffType : all)
            allStaffTypes.remove(staffType);
    }
}
