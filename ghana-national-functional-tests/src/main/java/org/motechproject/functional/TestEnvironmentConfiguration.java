package org.motechproject.functional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestEnvironmentConfiguration {
    @Value("#{functionalTestProperties['host']}")
    private String host;

    @Value("#{functionalTestProperties['port']}")
    private String port;

    public String host() {
        return host;
    }

    public String port() {
        return port;
    }
}
