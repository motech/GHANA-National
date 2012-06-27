package org.motechproject.ghana.national.domain.ivr;

import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.decisiontree.model.*;
import org.motechproject.ghana.national.domain.AlertType;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.repository.AllSpringBeans;
import org.motechproject.ghana.national.service.ExecuteAsOpenMRSAdmin;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.server.verboice.domain.Hangup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.INVALID_MOTECH_ID_PROMPT;
import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.NO_MESSAGE_IN_OUTBOX;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.valueOf;
import static org.motechproject.ghana.national.repository.AllPatientsOutbox.AUDIO_CLIP_NAME;
import static org.motechproject.ghana.national.repository.AllPatientsOutbox.TYPE;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ValidateMotechIdTransition extends Transition {

    @JsonProperty
    String language;

    @Autowired
    PlayMessagesFromOutboxTree playMessagesFromOutboxTree;

    @JsonProperty
    int pendingRetries;

    // Required for Ektorp
    public ValidateMotechIdTransition() {}

    public ValidateMotechIdTransition(String language, int pendingRetries) {
        this.language = language;
        this.pendingRetries = pendingRetries;
    }

    @Override
    public Node getDestinationNode(String motechId) {
        if (!isValidMotechId(motechId)) {
            return invalidMotechIdTransition();
        } else if (hasValidMobileMidwifeVoiceRegistration(motechId)) {
            return playMessagesFromOutboxTree.play(motechId, language);
        } else {
            return invalidMotechIdTransition();
        }

    }

    private boolean isValidMotechId(final String input) {
        ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin = (ExecuteAsOpenMRSAdmin) AllSpringBeans.applicationContext.getBean("executeAsOpenMRSAdmin");

        return executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                PatientService patientService = (PatientService) AllSpringBeans.applicationContext.getBean("patientService");
                return patientService.getPatientByMotechId(trimInputForTrailingHash(input)) != null;
            }
        });
    }

    private Node invalidMotechIdTransition() {
        String invalidMotechIdPromptURL = ((IVRClipManager) AllSpringBeans.applicationContext.getBean("ivrClipManager")).urlFor(INVALID_MOTECH_ID_PROMPT.value(), valueOf(language));
        Node node = new Node().addPrompts(new AudioPrompt().setAudioFileUrl(invalidMotechIdPromptURL));
        if (pendingRetries != 0)
            node.addTransition("?", new ValidateMotechIdTransition(language, pendingRetries - 1));
        return node;
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

    private boolean hasValidMobileMidwifeVoiceRegistration(String input) {
        MobileMidwifeService mobileMidwifeService = (MobileMidwifeService) AllSpringBeans.applicationContext.getBean("mobileMidwifeService");
        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(trimInputForTrailingHash(input));
        return midwifeEnrollment != null && midwifeEnrollment.getMedium().equals(Medium.VOICE);
    }
}
