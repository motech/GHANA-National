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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.INVALID_MOTECH_ID_PROMPT;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.EN;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.valueOf;

public class MotechIdValidationTransition extends Transition {

    @Value("#{ghanaNationalProperties['callcenter.dtmf.timeout']}")
    private String callCenterDtmfTimeout;

    @Value("#{ghanaNationalProperties['callcenter.no.of.digits.in.motech.id']}")
    private String noOfDigitsInMotechId;

    @JsonProperty
    String language;

    @Autowired
    private PlayMessagesFromOutboxTree playMessagesFromOutboxTree;

    @Autowired
    private ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin;

    @Autowired
    private PatientService patientService;

    @Autowired
    private IVRClipManager ivrClipManager;

    @Autowired
    private MobileMidwifeService mobileMidwifeService;

    @Autowired
    private ConnectToCallCenterTree connectToCallCenterTree;

    @JsonProperty
    int pendingRetries;

    // Required for Ektorp
    public MotechIdValidationTransition() {
    }

    private Logger logger = LoggerFactory.getLogger(MotechIdValidationTransition.class);


    public MotechIdValidationTransition clone(int pendingRetries) {
        MotechIdValidationTransition newTransition = new MotechIdValidationTransition(getLanguage(), pendingRetries);
        newTransition.callCenterDtmfTimeout = getCallCenterDtmfTimeout();
        newTransition.noOfDigitsInMotechId = getNoOfDigitsInMotechId();
        newTransition.playMessagesFromOutboxTree = getPlayMessagesFromOutboxTree();
        newTransition.executeAsOpenMRSAdmin = getExecuteAsOpenMRSAdmin();
        newTransition.patientService = getPatientService();
        newTransition.ivrClipManager = getIvrClipManager();
        newTransition.mobileMidwifeService = getMobileMidwifeService();
        newTransition.connectToCallCenterTree = getConnectToCallCenterTree();
        return newTransition;
    }

    public String getLanguage() {
        return language;
    }

    public int getPendingRetries() {
        return pendingRetries;
    }

    public MotechIdValidationTransition(String language, int pendingRetries) {
        this.language = language;
        this.pendingRetries = pendingRetries;
    }

    @Override
    public Node getDestinationNode(String input, FlowSession session) {
        String callerPhoneNumber = (String) session.get("From");
        try {
            if (!isValidMotechId(input)) {
                return invalidMotechIdTransition(callerPhoneNumber);
            } else if (hasValidMobileMidwifeVoiceRegistration(input)) {
                return playMessagesFromOutboxTree.play(input, language, callerPhoneNumber);
            } else {
                return invalidMotechIdTransition(callerPhoneNumber);
            }
        }catch (Exception e){
            logger.error("Encountered error while validating user for IVR: " + input, e);
            return new Node().addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(AudioPrompts.ERROR_ALERT.value(), EN)));
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

    private Node invalidMotechIdTransition(String callerPhoneNumber) {
        String invalidMotechIdPromptURL = ivrClipManager.urlFor(INVALID_MOTECH_ID_PROMPT.value(), valueOf(language));
        Node node = new Node();
        if (pendingRetries != 1) {
            node.setTransitionTimeout(callCenterDtmfTimeout);
            // As requested by david, we expect 7 digits for motech id instead of finish on key
            //  node.setTransitionFinishOnKey(callCenterFinishOnKey);
            node.setTransitionNumDigits(noOfDigitsInMotechId);
            node.addTransitionPrompts(new AudioPrompt().setAudioFileUrl(invalidMotechIdPromptURL));
            node.addTransition("0", new Transition().setDestinationNode(connectToCallCenterTree.getAsNode(valueOf(language), callerPhoneNumber)));
            node.addTransition("*", new Transition().setDestinationNode(connectToCallCenterTree.getAsNode(valueOf(language), callerPhoneNumber)));
            node.addTransition("timeout", new Transition().setDestinationNode(connectToCallCenterTree.getAsNode(valueOf(language), callerPhoneNumber)));
            node.addTransition("?", this.clone(this.pendingRetries - 1));
        }
        return node;
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

    private boolean hasValidMobileMidwifeVoiceRegistration(String patientId) {
        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(trimInputForTrailingHash(patientId));
        return midwifeEnrollment != null && midwifeEnrollment.getMedium().equals(Medium.VOICE);
    }

    public String getCallCenterDtmfTimeout() {
        return callCenterDtmfTimeout;
    }

    public String getNoOfDigitsInMotechId() {
        return noOfDigitsInMotechId;
    }

    public PlayMessagesFromOutboxTree getPlayMessagesFromOutboxTree() {
        return playMessagesFromOutboxTree;
    }

    public ExecuteAsOpenMRSAdmin getExecuteAsOpenMRSAdmin() {
        return executeAsOpenMRSAdmin;
    }

    public PatientService getPatientService() {
        return patientService;
    }

    public IVRClipManager getIvrClipManager() {
        return ivrClipManager;
    }

    public MobileMidwifeService getMobileMidwifeService() {
        return mobileMidwifeService;
    }

    public ConnectToCallCenterTree getConnectToCallCenterTree() {
        return connectToCallCenterTree;
    }
}
