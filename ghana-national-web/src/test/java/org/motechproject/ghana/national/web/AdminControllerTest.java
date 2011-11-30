package org.motechproject.ghana.national.web;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AdminControllerTest {
    @Test
    public void shouldDisplayDashboard() {
        final AdminController controller = new AdminController();
        final String view = controller.dashboard();
        assertEquals("common/dashboard", view);
    }
}
