package org.motechproject.ghana.national.builder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IVRCallbackUrlBuilder {

    @Value("#{ghanaNationalProperties['host']}")
    private String host;
    @Value("#{ghanaNationalProperties['port']}")
    private String port;
    @Value("#{ghanaNationalProperties['context.path']}")
    private String contextPath;

    public String outboundCallUrl(String patientId, String language, String decisionTree) {
        return "http://" + host + ":" + port + "/" + contextPath + "/outgoing/call?motechId=" + patientId + "&ln=" + language + "&tree=" + decisionTree;
    }
}
