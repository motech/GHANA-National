package org.motechproject.ghana.national.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class IdentifierServiceTest {

    private IdentifierService identifierService;

    @Mock
    private HttpClient httpClient;

    @Before
    public void setUp() {
        initMocks(this);
        identifierService = new IdentifierService();
        ReflectionTestUtils.setField(identifierService, "url", "url");
        ReflectionTestUtils.setField(identifierService, "httpClient", httpClient);
        ReflectionTestUtils.setField(identifierService, "user", "user");
        ReflectionTestUtils.setField(identifierService, "password", "password");
    }

    @Test
    public void shouldCallTheOmodServiceWithQueryParams() throws IOException {
        identifierService.newFacilityId();

        ArgumentCaptor<HttpMethod> captor = ArgumentCaptor.forClass(HttpMethod.class);
        verify(httpClient).executeMethod(captor.capture());
        GetMethod getMethod = (GetMethod) captor.getValue();
        assertEquals("generator=MoTeCH+Facility+ID+Generator&type=MoTeCH+Facility+Id&user=user&password=password", getMethod.getQueryString());
    }

    @Test
    public void shouldCallServiceWithQueryParamsForStaffID() throws IOException {
        identifierService.newStaffId();

        ArgumentCaptor<HttpMethod> captor = ArgumentCaptor.forClass(HttpMethod.class);
        verify(httpClient).executeMethod(captor.capture());
        GetMethod getMethod = (GetMethod) captor.getValue();
        assertEquals("generator=MoTeCH+Staff+ID+Generator&type=MoTeCH+Staff+Id&user=user&password=password", getMethod.getQueryString());
    }

    @Test
    public void shouldCallServiceWithQueryParamsForPatientId() throws IOException {
        identifierService.newPatientId();

        ArgumentCaptor<HttpMethod> captor = ArgumentCaptor.forClass(HttpMethod.class);
        verify(httpClient).executeMethod(captor.capture());
        GetMethod getMethod = (GetMethod) captor.getValue();
        assertEquals("generator=MoTeCH+ID+Generator&type=MoTeCH+Id&user=user&password=password", getMethod.getQueryString());
    }
}


