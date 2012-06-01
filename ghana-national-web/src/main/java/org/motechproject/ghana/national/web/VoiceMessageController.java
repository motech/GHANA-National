package org.motechproject.ghana.national.web;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllPatientOutboxes;
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
    public static final String PLAY_MESSAGE_URL = "/verboice-service/playMessage";

    private VerboiceIVRService verboiceIVRService;
    private PatientService patientService;
    private AllPatientOutboxes allPatientOutboxes;

    public VoiceMessageController() {
    }

    @Autowired
    public VoiceMessageController(VerboiceIVRService verboiceIVRService, PatientService patientService, AllPatientOutboxes allPatientOutboxes) {
        this.verboiceIVRService = verboiceIVRService;
        this.patientService = patientService;
        this.allPatientOutboxes = allPatientOutboxes;
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
                        return redirectToDecisionTree(treeName, "1", treePath, language);
                    } else {
                        allPatientOutboxes.addUrlToOutbox(audioURL(request), digits);
                        return redirectToPlayMessageController(digits, null);
                    }
                }
            }
            return redirectToDecisionTree(treeName, digits, treePath, language);
        }
        return verboiceIVRService.getHandler().handle(request.getParameterMap());
    }

    @RequestMapping("/playMessage")
    public String playMessage(HttpServletRequest request, ModelMap modelMap) throws IOException {
//        ModelAndView modelAndView = new ModelAndView();

        String externalId = request.getParameter("externalId");
        String language = request.getParameter("language");

        VerboiceResponse verboiceResponse = new VerboiceResponse();
        List<String> audioUrls = allPatientOutboxes.getAudioUrlsFor(externalId, language);
        for (String audioUrl : audioUrls) {
            verboiceResponse.playUrl(audioUrl);
        }

        modelMap.put("playContent",verboiceResponse.toXMLString());
        return "playMessages";
    }

    private String redirectToPlayMessageController(String externalId, String language) {
        return String.format("forward:/%s?language=%s&externalId=%s", PLAY_MESSAGE_URL, language, externalId)
                .replaceAll("//", "/");
    }

    private String audioURL(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/resource/stream/en/welcome.wav";
    }

    private String redirectToDecisionTree(String treeName, String digits, String treePath, String language) {
        final String transitionKey = digits == null ? "" : "&trK=" + digits;
        return String.format("forward:/%s?type=verboice&tree=%s&trP=%s&ln=%s%s", DECISIONTREE_URL, treeName, treePath, language, transitionKey)
                .replaceAll("//", "/");
    }
}
