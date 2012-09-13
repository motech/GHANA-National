package org.motechproject.ghana.national.domain.ivr.transition;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.AudioPrompt;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.PlayMessagesFromOutboxTree;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.motechproject.ghana.national.domain.mobilemidwife.Language.EN;

public class PreferredLanguageTransition extends Transition {
    @Autowired
    PlayMessagesFromOutboxTree playMessagesFromOutboxTree;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @Autowired
    private IVRClipManager ivrClipManager;

    private Logger logger = LoggerFactory.getLogger(PreferredLanguageTransition.class);

    @Override
    public Node getDestinationNode(String input, FlowSession session) {
        try {
            MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(trimInputForTrailingHash(input));
            Language language = midwifeEnrollment != null ? midwifeEnrollment.getLanguage() : Language.EN;
            return playMessagesFromOutboxTree.play(input, language.name(), midwifeEnrollment.getPhoneNumber());
        }catch (Exception e){
            logger.error("Encountered error while playing clips to the user: " + input, e);
            return new Node().addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(AudioPrompts.ERROR_ALERT.value(), EN)));
        }
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

}
