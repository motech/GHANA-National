package org.motechproject.ghana.national.repository;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;

import static org.motechproject.ghana.national.domain.Constants.*;

@Repository
public class IdentifierGenerator {
    private Logger log = LoggerFactory.getLogger(IdentifierGenerator.class);
    private static final String ID_GENERATOR = "generator";
    private static final String ID_TYPE = "type";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    private HttpClient httpClient;

    @Value("#{ghanaNationalProperties['omod.identifier.service.path']}")
    private String path;

    @Value("#{ghanaNationalProperties['omod.identifier.service.port']}")
    private String port;

    @Value("#{ghanaNationalProperties['omod.identifier.service.host']}")
    private String host;

    @Value("#{ghanaNationalProperties['omod.user.name']}")
    private String user;

    @Value("#{ghanaNationalProperties['omod.user.password']}")
    private String password;
    private static final String OPENMRS_PORT = "OPENMRS_PORT";

    public IdentifierGenerator() {
        httpClient = new HttpClient();
    }

    public String newPatientId() {
        return callOmodService(IDGEN_SEQ_ID_GEN_PATIENT_ID, PATIENT_IDENTIFIER_TYPE_PATIENT_ID);
    }

    public String newStaffId() {
        return callOmodService(IDGEN_SEQ_ID_GEN_STAFF_ID, PATIENT_IDENTIFIER_TYPE_STAFF_ID);
    }

    public String newFacilityId() {
        return callOmodService(IDGEN_SEQ_ID_GEN_FACILITY_ID, PATIENT_IDENTIFIER_TYPE_FACILITY_ID);
    }

    private String callOmodService(String generatorName, String idTypeName) {
        try {
            String url = String.format("http://%s:%s/%s", host, System.getProperty(OPENMRS_PORT, port), path);
            GetMethod getMethod = new GetMethod(url);
            getMethod.setQueryString(
                    new NameValuePair[]{
                            new NameValuePair(ID_GENERATOR, generatorName),
                            new NameValuePair(ID_TYPE, idTypeName),
                            new NameValuePair(USER, user),
                            new NameValuePair(PASSWORD, password)
                    });
            httpClient.executeMethod(getMethod);
            String generatedId = getMethod.getResponseBodyAsString();
            log.info("Generated Id for Type-" + idTypeName + " is " + generatedId);
            return generatedId;
        } catch (IOException e) {
            log.error("Error", e);
        }
        return StringUtils.EMPTY;
    }
}

