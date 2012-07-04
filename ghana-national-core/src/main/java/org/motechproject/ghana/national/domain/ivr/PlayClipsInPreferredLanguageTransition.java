package org.motechproject.ghana.national.domain.ivr;

import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.springframework.beans.factory.annotation.Autowired;

public class PlayClipsInPreferredLanguageTransition extends Transition {
    @Autowired
    PlayMessagesFromOutboxTree playMessagesFromOutboxTree;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Override
    public Node getDestinationNode(String input, FlowSession session) {
        Language language = getMobileMidwifeLanguage(input);
        return playMessagesFromOutboxTree.play(input, language.name());
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

    private Language getMobileMidwifeLanguage(String patientId) {
        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(trimInputForTrailingHash(patientId));
        return midwifeEnrollment != null ? midwifeEnrollment.getLanguage(): Language.EN;
    }

}
