package org.motechproject.ghana.national.web;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.web.VoiceMessageController.*;

public class VoiceMessageControllerTest {

    @InjectMocks
    private VoiceMessageController voiceMessageController = new VoiceMessageController();

    @Mock
    AllPatientsOutbox mockPatientOutbox;

    @Mock
    PatientService mockPatientService;

    @Mock
    MobileMidwifeService mockMobileMidwifeService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateMotechIdAndRedirectItToPlay() {
        MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest();
        String externalId = "1234567";
        String language = "en";
        mockHttpRequest.setParameter("Digits", externalId);
        mockHttpRequest.setParameter("trP", new String(Base64.encodeBase64("/1".getBytes())));
        mockHttpRequest.setParameter("ln", language);
        mockHttpRequest.setParameter("tree", "treeName");

        when(mockPatientService.getPatientByMotechId(externalId)).thenReturn(new Patient());
        when(mockMobileMidwifeService.findActiveBy(externalId)).thenReturn(new MobileMidwifeEnrollment(DateTime.now()).setMedium(Medium.VOICE));


        String redirectUrl = voiceMessageController.handleRequest(mockHttpRequest);

        assertThat(redirectUrl, is(String.format("forward:/%s?language=%s&externalId=%s", PLAY_MESSAGE_URL, language, externalId).replaceAll("//", "/")));
    }

    @Test
    public void shouldPlayErrorMessageIfThereIsNoActiveMobileMidwifeRegistrationForTheGivenMotechId() {
        MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest();
        String externalId = "1234567";
        String language = "en";
        mockHttpRequest.setParameter("Digits", externalId);
        mockHttpRequest.setParameter("trP", new String(Base64.encodeBase64("/1".getBytes())));
        mockHttpRequest.setParameter("ln", language);
        mockHttpRequest.setParameter("tree", "treeName");

        when(mockPatientService.getPatientByMotechId(externalId)).thenReturn(new Patient());
        when(mockMobileMidwifeService.findActiveBy(externalId)).thenReturn(null);
        String redirectUrl = voiceMessageController.handleRequest(mockHttpRequest);

        assertThat(redirectUrl, is(String.format("forward:/%s", PLAY_INVALID_REGISTRATION).replaceAll("//", "/")));
    }

    @Test
    public void shouldValidateMotechIdAndRedirectItToDecisionTreeForInvalidMotechId() {
        MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest();
        String externalId = "1234567";
        String language = "en";
        mockHttpRequest.setParameter("Digits", externalId);
        mockHttpRequest.setParameter("trP", new String(Base64.encodeBase64("/1".getBytes())));
        mockHttpRequest.setParameter("ln", language);
        mockHttpRequest.setParameter("tree", "treeName");

        when(mockPatientService.getPatientByMotechId(externalId)).thenReturn(null);

        String redirectUrl = voiceMessageController.handleRequest(mockHttpRequest);

        assertThat(redirectUrl, is(String.format("forward:/%s", PLAY_INVALID_MOTECH_ID).replaceAll("//", "/")));

    }

    @Test
    public void shouldPlayMessageFromOutbox() throws IOException {
        MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest();
        String externalId = "1234567";
        String language = "en";
        mockHttpRequest.setParameter("externalId", externalId);
        mockHttpRequest.setParameter("language", language);
        ModelMap modelMap = new ModelMap();

        List<String> audioUrls = Arrays.asList("url1", "url2");
        when(mockPatientOutbox.getAudioFileNames(externalId)).thenReturn(audioUrls);
        when(mockMobileMidwifeService.findActiveBy(externalId)).thenReturn(new MobileMidwifeEnrollment(DateTime.now()).setMedium(Medium.VOICE));

        voiceMessageController.playMessage(mockHttpRequest, modelMap);

        String playContent = (String) modelMap.get("playContent");
        assertTrue(playContent.contains("<Play loop=\"1\">url1</Play>"));
        assertTrue(playContent.contains("<Play loop=\"1\">url2</Play>"));
    }

}
