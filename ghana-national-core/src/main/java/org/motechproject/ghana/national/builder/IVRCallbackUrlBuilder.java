package org.motechproject.ghana.national.builder;

import org.motechproject.MotechException;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class IVRCallbackUrlBuilder {

    @Value("#{ghanaNationalProperties['host']}")
    private String host;
    @Value("#{ghanaNationalProperties['port']}")
    private String port;
    @Value("#{ghanaNationalProperties['context.path']}")
    private String contextPath;

    public String outboundCallUrl(String patientId) {
        String url = "http://" + host + ":" + port + "/" + contextPath + "/verboice/ivr?&Digits="+ patientId +"&type=verboice&ln=en&tree=OutboundDecisionTree&trP=Lw";
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MotechException("Encountered exception while encoding verboice outbound url, " + url);
        }
    }

    public String callCenterDialStatusUrl(Language language){
        return  "http://" + host + ":" + port + "/" + contextPath + "/ivr/dial/" + language.name() + "/callback";
    }
}
