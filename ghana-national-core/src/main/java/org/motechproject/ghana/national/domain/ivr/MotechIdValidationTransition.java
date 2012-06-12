package org.motechproject.ghana.national.domain.ivr;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;

import java.util.List;

public class MotechIdValidationTransition extends Transition {
    private MobileMidwifeService mobileMidwifeService;
    private PatientService patientService;
    private AllPatientsOutbox allPatientsOutbox;
    private IVRClipManager ivrClipManager;

    public MotechIdValidationTransition() {
//        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
//        mobileMidwifeService = (MobileMidwifeService) context.getBean("mobileMidwifeService");
//        patientService = (PatientService) context.getBean("patientService");
//        allPatientsOutbox = (AllPatientsOutbox) context.getBean("allPatientsOutbox");
//        ivrClipManager = (IVRClipManager) context.getBean("ivrClipManager");
    }

    @Override
    // TODO: Replace with actual audio stream
    public Node getDestinationNode(String input) {
        if (!isValidMotechIdentifier(input)) {
            return new Node().addPrompts(new TextToSpeechPrompt().setMessage("Invalid Motech Id"));
        } else if (hasValidMobileMidwifeVoiceRegistration(input)) {
            return sendResponseFromOutbox(input);
        } else {
            return new Node().addPrompts(new TextToSpeechPrompt().setMessage("No Mobile midwife registration for this motech id"));
        }
    }

    private Node sendResponseFromOutbox(String motechId) {
        List<String> audioUrls = allPatientsOutbox.getAudioFileNames(motechId);
        Node node = new Node();
        for (String audioUrl : audioUrls) {
            node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(audioUrl, Language.valueOf(getName()))));
        }
        return node;
    }

    private boolean isValidMotechIdentifier(String input) {
        input = trimInputForTrailingHash(input);
        return patientService.getPatientByMotechId(input) != null;
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

    private boolean hasValidMobileMidwifeVoiceRegistration(String input) {
        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(input);
        if (midwifeEnrollment != null && midwifeEnrollment.getMedium().equals(Medium.VOICE)) {
            // TODO: Adding dummy message to Outbox, temp fix until other audio/stream are ready.
            //allPatientsOutbox.addAudioFileName(digits, audioURL(request), sdkfl);
            return true;
        }
        return false;
    }

}
