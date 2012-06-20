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
import org.motechproject.ghana.national.repository.AllSpringBeans;
import org.motechproject.ghana.national.service.ExecuteAsOpenMRSAdmin;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.context.ApplicationContext;
import sun.awt.AppContext;

import java.util.List;

public class MotechIdValidationTransition extends Transition {

    @Override
    public Node getDestinationNode(String input) {
        ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin = (ExecuteAsOpenMRSAdmin) AllSpringBeans.applicationContext.getBean("executeAsOpenMRSAdmin");

        if (!isValidMotechIdentifier(input, executeAsOpenMRSAdmin)) {
            return new Node().addPrompts(new TextToSpeechPrompt().setMessage("Invalid Motech Id"));
        }
        return null;
// else if (hasValidMobileMidwifeVoiceRegistration(input)) {
//            return sendResponseFromOutbox(input);
//        } else {
//            return new Node().addPrompts(new TextToSpeechPrompt().setMessage("No Mobile midwife registration for this motech id"));
//        }
    }

    private Node sendResponseFromOutbox(String motechId) {
        AllPatientsOutbox allPatientsOutbox = (AllPatientsOutbox) AllSpringBeans.applicationContext.getBean("allPatientsOutbox");
        IVRClipManager ivrClipManager = (IVRClipManager) AllSpringBeans.applicationContext.getBean("ivrClipManager");
        List<String> audioUrls = allPatientsOutbox.getAudioFileNames(motechId);
        Node node = new Node();
        for (String audioUrl : audioUrls) {
            node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(audioUrl, Language.valueOf(getName()))));
        }
        return node;
    }

    private boolean isValidMotechIdentifier(final String input, ExecuteAsOpenMRSAdmin executeAsOpenMRSAdmin) {
        return executeAsOpenMRSAdmin.execute(new ExecuteAsOpenMRSAdmin.OpenMRSServiceCall() {
            @Override
            public Object invoke() {
                PatientService patientService = (PatientService) AllSpringBeans.applicationContext.getBean("patientService");
                return patientService.getPatientByMotechId(trimInputForTrailingHash(input)) != null;
            }
        });
    }

    private String trimInputForTrailingHash(String input) {
        return input.replaceAll("#", "");
    }

    private boolean hasValidMobileMidwifeVoiceRegistration(String input) {
        MobileMidwifeService mobileMidwifeService = (MobileMidwifeService) AllSpringBeans.applicationContext.getBean("mobileMidwifeService");

        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findActiveBy(input);
        if (midwifeEnrollment != null && midwifeEnrollment.getMedium().equals(Medium.VOICE)) {
            // TODO: Adding dummy message to Outbox, temp fix until other audio/stream are ready.
            //allPatientsOutbox.addAudioFileName(digits, audioURL(request), sdkfl);
            return true;
        }
        return false;
    }

}
