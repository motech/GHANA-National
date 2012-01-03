package org.motechproject.functional.framework;

import org.motechproject.functional.TestEnvironmentConfiguration;

public class ApplicationURLs {
    public static final String LOGIN_PATH = "/ghana-national-web/login.jsp";
    public static final String OPENMRS_PATH = "/openmrs";

    private TestEnvironmentConfiguration configuration;

    public ApplicationURLs(TestEnvironmentConfiguration configuration) {
        this.configuration = configuration;
    }

    public String forHomePage() {
        return forPath(LOGIN_PATH);
    }

    public String forPath(String path) {
        return String.format("http://%s:%s%s", configuration.host(), configuration.port(), path);
    }

    public String forOpenMRSHomePage() {
        return forPath(OPENMRS_PATH);
    }
}
