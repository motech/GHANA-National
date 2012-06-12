package org.motechproject.ghana.national.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.server.verboice.domain.VerboiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/outgoing")
public class OutgoingCallController {

    @Autowired
    private AllPatientsOutbox allPatientsOutbox;
    @Autowired
    private IVRClipManager ivrClipManager;

    public OutgoingCallController() {
    }

    @RequestMapping("/call")
    @ResponseBody
    public String call(HttpServletRequest request) {
        String motechId = request.getParameter("motechId");
        if (StringUtils.isBlank(motechId)) {
            return "<Response>\n" +
                    "<Say>Unexpected error</Say>\n" +
                    "</Response>";
        }
        String language = request.getParameter("ln");
        List messages = allPatientsOutbox.getAudioFileNames(motechId);
        VerboiceResponse verboiceResponse = new VerboiceResponse();
        for (Object audioUrl : messages) {
            verboiceResponse.playUrl(ivrClipManager.urlFor((String) audioUrl, Language.valueOf(language)));
        }
        return verboiceResponse.toXMLString();
    }
}
