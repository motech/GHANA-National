package org.motechproject.ghana.national.functional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestEnvironmentConfiguration {
    @Value("#{functionalTestProperties['host']}")
    private String host;

    @Value("#{functionalTestProperties['port']}")
    private String port;

    @Value("#{ghanaNationalProperties['omod.identifier.service.host']}")
    private String openMRSHost;

    @Value("#{ghanaNationalProperties['omod.identifier.service.port']}")
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
        return System.getProperty("OPENMRS_PORT", openMRSPort);
    }
}
