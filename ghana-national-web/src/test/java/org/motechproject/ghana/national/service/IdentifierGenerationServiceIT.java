package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ResourceBundle;

import static junit.framework.Assert.assertNotNull;

public class IdentifierGenerationServiceIT {

    IdentifierGenerationService service;

    @Before
    public void setUp() {
        ResourceBundle bundle = ResourceBundle.getBundle("ghana-national");
        service = new IdentifierGenerationService();
        ReflectionTestUtils.setField(service, "url", bundle.getString("omod.identifier.service.url"));
        ReflectionTestUtils.setField(service, "user", bundle.getString("omod.user.name"));
        ReflectionTestUtils.setField(service, "password", bundle.getString("omod.user.password"));
    }

    @Test
    public void shouldGenerateIDForAnIDTypeAndGenerator() throws IOException {
        assertNotNull(service.newFacilityId());
        assertNotNull(service.newPatientId());
        assertNotNull(service.newStaffId());
    }
}
