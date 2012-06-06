package org.motechproject.ghana.national.web;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.motechproject.server.verboice.domain.VerboiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/verboice-service")
public class VoiceMessageController {

    public static final String DECISIONTREE_URL = "/decisiontree/node";
    public static final String PLAY_INVALID_REGISTRATION = "/verboice-service/playInvalidRegistration";
    public static final String PLAY_INVALID_MOTECH_ID = "/verboice-service/playInvalidMotechId";
    public static final String PLAY_MESSAGE_URL = "/verboice-service/playMessage";

    private VerboiceIVRService verboiceIVRService;
    private PatientService patientService;
    private AllPatientsOutbox allPatientsOutbox;
    private MobileMidwifeService mobileMidwifeService;

    public VoiceMessageController() {
    }

    @Autowired
    public VoiceMessageController(VerboiceIVRService verboiceIVRService, PatientService patientService, AllPatientsOutbox allPatientsOutbox, MobileMidwifeService mobileMidwifeService) {
        this.verboiceIVRService = verboiceIVRService;
        this.patientService = patientService;
        this.allPatientsOutbox = allPatientsOutbox;
        this.mobileMidwifeService = mobileMidwifeService;
    }

    @RequestMapping("/ivr")
    @ApiSession
    @LoginAsAdmin
    public String handleRequest(HttpServletRequest request) {
        final String treeName = request.getParameter("tree");
        if (StringUtils.isNotBlank(treeName)) {

            String digits = request.getParameter("Digits");
            String treePath = request.getParameter("trP");
            String language = request.getParameter("ln");
            if (StringUtils.isNotEmpty(treePath)) {
                String path = new String(Base64.decodeBase64(treePath));
                if (path.length() >= 2) { // '/1', '/2' will be the inputs for language preference. TODO: Remove this logic of length
                    Patient patient = patientService.getPatientByMotechId(digits);
                    if (patient == null) {
                        return String.format("forward:/%s", PLAY_INVALID_MOTECH_ID).replaceAll("//", "/");
                    } else if (hasValidMobileMidwifeVoiceRegistration(request, digits)) {
                        return redirectToPlayMessageController(digits, language);
                    } else {
                        return String.format("forward:/%s", PLAY_INVALID_REGISTRATION).replaceAll("//", "/");
                    }
                }
            }
            return redirectToDecisionTree(treeName, digits, treePath, language);
        }
        return verboiceIVRService.getHandler().handle(request.getParameterMap());
    }

    @RequestMapping("/playMessage")
    public String playMessage(HttpServletRequest request, ModelMap modelMap) throws IOException {
        String externalId = request.getParameter("externalId");
        String language = request.getParameter("language");

        VerboiceResponse verboiceResponse = new VerboiceResponse();
        List<String> audioUrls = allPatientsOutbox.getAudioUrlsFor(externalId, language);
        for (String audioUrl : audioUrls) {
            verboiceResponse.playUrl(audioUrl);
        }

        modelMap.put("playContent", verboiceResponse.toXMLString());
        return "playMessages";
    }

    @RequestMapping("/playInvalidMotechId")
    public String playInvalidMotechId(HttpServletRequest request, ModelMap modelMap) throws IOException {
        VerboiceResponse verboiceResponse = new VerboiceResponse();
        // TODO: Replace with actual audio stream
        verboiceResponse.say("Invalid Motech Id");
        modelMap.put("playContent", verboiceResponse.toXMLString());
        return "playMessages";

    }

    @RequestMapping("/playInvalidRegistration")
    public String playInvalidRegistration(HttpServletRequest request, ModelMap modelMap) throws IOException {
        VerboiceResponse verboiceResponse = new VerboiceResponse();
        // TODO: Replace with actual audio stream
        verboiceResponse.say("No Mobile midwife registration for this motech id");
        modelMap.put("playContent", verboiceResponse.toXMLString());
        return "playMessages";

    }

    private boolean hasValidMobileMidwifeVoiceRegistration(HttpServletRequest request, String digits) {
        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(digits);
        if (midwifeEnrollment != null && midwifeEnrollment.getMedium().equals(Medium.VOICE)) {
            // TODO: Adding dummy message to Outbox, temp fix until other audio/stream are ready.
            //allPatientsOutbox.addAudioClip(digits, audioURL(request), sdkfl);
            return true;
        }
        return false;
    }

    private String audioURL(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/resource/stream/en/welcome.wav";
    }

    private String redirectToPlayMessageController(String externalId, String language) {
        return String.format("forward:/%s?language=%s&externalId=%s", PLAY_MESSAGE_URL, language, externalId)
                .replaceAll("//", "/");
    }

    private String redirectToDecisionTree(String treeName, String digits, String treePath, String language) {
        final String transitionKey = digits == null ? "" : "&trK=" + digits;
        return String.format("forward:/%s?type=verboice&tree=%s&trP=%s&ln=%s%s", DECISIONTREE_URL, treeName, treePath, language, transitionKey)
                .replaceAll("//", "/");
    }
}
