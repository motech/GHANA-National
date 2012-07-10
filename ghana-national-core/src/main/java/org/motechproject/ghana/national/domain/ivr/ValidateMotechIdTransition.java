package org.motechproject.ghana.national.domain.ivr;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.ExecuteAsOpenMRSAdmin;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.INVALID_MOTECH_ID_PROMPT;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.valueOf;

public class ValidateMotechIdTransition extends Transition {

    @JsonProperty
    String language;

    @Autowired
    PlayMessagesFromOutboxTree playMessagesFromOutboxTree;

    @Autowired
    ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin;

    @Autowired
    PatientService patientService;

    @Autowired
    IVRClipManager ivrClipManager;

    @Autowired
    MobileMidwifeService mobileMidwifeService;

    @JsonProperty
    int pendingRetries;

    // Required for Ektorp
    public ValidateMotechIdTransition() {}

    public ValidateMotechIdTransition(String language, int pendingRetries) {
        this.language = language;
        this.pendingRetries = pendingRetries;
    }

    @Override
    public Node getDestinationNode(String input, FlowSession session) {
        if (!isValidMotechId(input)) {
            return invalidMotechIdTransition();
        } else if (hasValidMobileMidwifeVoiceRegistration(input)) {
            return playMessagesFromOutboxTree.play(input, language);
        } else {
            return invalidMotechIdTransition();
        }

    }

    private boolean isValidMotechId(final String input) {
        return executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                return patientService.getPatientByMotechId(trimInputForTrailingHash(input)) != null;
            }
        });
    }

    private Node invalidMotechIdTransition() {
        String invalidMotechIdPromptURL = ivrClipManager.urlFor(INVALID_MOTECH_ID_PROMPT.value(), valueOf(language));
        Node node = new Node().addPrompts(new AudioPrompt().setAudioFileUrl(invalidMotechIdPromptURL));
        if (pendingRetries != 0)
            node.addTransition("?", new ValidateMotechIdTransition(language, pendingRetries - 1));
        return node;
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

    private boolean hasValidMobileMidwifeVoiceRegistration(String patientId) {
        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(trimInputForTrailingHash(patientId));
        return midwifeEnrollment != null && midwifeEnrollment.getMedium().equals(Medium.VOICE);
    }
}
