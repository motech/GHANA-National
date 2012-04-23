package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Before;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.openmrs.security.OpenMRSSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseOpenMrsIntegrationTest extends BaseIntegrationTest {


    @Value("#{openmrsProperties['openmrs.admin.username']}")
    private String userName;

    @Value("#{openmrsProperties['openmrs.admin.password']}")
    private String password;

    @Autowired
    private OpenMRSSession session;

    @Before
    public final void before() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(OpenMRSSession.login(userName, password), password));
        session.open();
        session.authenticate();
    }

    @After
    public final void after() {
        SecurityContextHolder.getContext().setAuthentication(null);
        session.close();
    }
}
