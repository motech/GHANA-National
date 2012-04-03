package org.motechproject.ghana.national.functional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestEnvironmentConfiguration {
    @Value("#{functionalTestProperties['host']}")
    private String host;

    @Value("#{functionalTestProperties['port']}")
    private String port;

    @Value("#{functionalTestProperties['openmrs_host']}")
    private String openMRSHost;

    @Value("#{functionalTestProperties['openmrs_port']}")
    private String openMRSPort;

    public String host() {
        return host;
    }

    public String port() {
        return port;
    }

    public String openMRSHost() {
        return openMRSHost;
    }

    public String openMRSPort() {
        return openMRSPort;
    }
}
