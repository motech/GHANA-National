package org.motechproject.ghana.national.domain.ivr;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.repository.AllSpringBeans;
import org.motechproject.ghana.national.service.ExecuteAsOpenMRSAdmin;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;

import java.util.List;

import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.INVALID_MOTECH_ID_PROMPT;
import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.NO_MESSAGE_IN_OUTBOX;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.valueOf;
import static org.springframework.util.CollectionUtils.isEmpty;

public class MotechIdValidationTransition extends Transition {

    @JsonProperty
    String language;

    // Required for Ektorp
    public MotechIdValidationTransition() {
    }

    public MotechIdValidationTransition(String language) {
        this.language = language;
    }

    @Override
    public Node getDestinationNode(String input) {
        if (!isValidMotechIdentifier(input)) {
            return invalidMotechIdNode();
        } else if (hasValidMobileMidwifeVoiceRegistration(input)) {
            return sendResponseFromOutbox(input);
        } else {
            return invalidMotechIdNode();
        }
    }

    private Node sendResponseFromOutbox(String motechId) {
        AllPatientsOutbox allPatientsOutbox = (AllPatientsOutbox) AllSpringBeans.applicationContext.getBean("allPatientsOutbox");
        IVRClipManager ivrClipManager = (IVRClipManager) AllSpringBeans.applicationContext.getBean("ivrClipManager");
        List<String> audioUrls = allPatientsOutbox.getAudioFileNames(motechId);
        Node node = new Node();
        if (isEmpty(audioUrls))
            return node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(NO_MESSAGE_IN_OUTBOX.value(), valueOf(language))));

        for (String audioUrl : audioUrls) {
            node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(audioUrl, valueOf(getName()))));
        }
        return node;
    }

    private boolean isValidMotechIdentifier(final String input) {
        ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin = (ExecuteAsOpenMRSAdmin) AllSpringBeans.applicationContext.getBean("executeAsOpenMRSAdmin");

        return executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                PatientService patientService = (PatientService) AllSpringBeans.applicationContext.getBean("patientService");
                return patientService.getPatientByMotechId(trimInputForTrailingHash(input)) != null;
            }
        });
    }

    private Node invalidMotechIdNode() {
        return new Node()
                .addPrompts(new AudioPrompt().setAudioFileUrl(((IVRClipManager) AllSpringBeans.applicationContext.getBean("ivrClipManager")).urlFor(INVALID_MOTECH_ID_PROMPT.value(), valueOf(language))))
                .addTransition("?", new MotechIdValidationTransition(language));
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

    private boolean hasValidMobileMidwifeVoiceRegistration(String input) {
        MobileMidwifeService mobileMidwifeService = (MobileMidwifeService) AllSpringBeans.applicationContext.getBean("mobileMidwifeService");

        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(trimInputForTrailingHash(input));
        if (midwifeEnrollment != null && midwifeEnrollment.getMedium().equals(Medium.VOICE)) {
            // TODO: Adding dummy message to Outbox, temp fix until other audio/stream are ready.
//            allPatientsOutbox.addAudioFileName(digits, audioURL(request), sdkfl);
            return true;
        }
        return false;
    }

}
