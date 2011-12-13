package org.motechproject.ghana.national.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class StaffTypeTest {

    @Test
    public void shouldCheckAdminForAdminRoles() {
        assertTrue(StaffType.Role.isAdmin("CallCenter Admin"));
        assertTrue(StaffType.Role.isAdmin("HealthCare Admin"));
        assertTrue(StaffType.Role.isAdmin("Super Admin"));
        assertTrue(StaffType.Role.isAdmin("Facility Admin"));
        assertFalse(StaffType.Role.isAdmin("Not an Admin"));
    }
}
