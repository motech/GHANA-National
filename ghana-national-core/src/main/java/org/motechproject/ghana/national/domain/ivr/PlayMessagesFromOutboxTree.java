package org.motechproject.ghana.national.domain.ivr;

import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.motechproject.decisiontree.model.*;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.motechproject.ghana.national.domain.AlertType.MOBILE_MIDWIFE;
import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.NO_MESSAGE_IN_OUTBOX;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.valueOf;
import static org.motechproject.ghana.national.repository.AllPatientsOutbox.AUDIO_CLIP_NAME;
import static org.motechproject.ghana.national.repository.AllPatientsOutbox.TYPE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class PlayMessagesFromOutboxTree {

    @Autowired
    AllPatientsOutbox allPatientsOutbox;

    @Autowired
    IVRClipManager ivrClipManager;

    @Value("#{ghanaNationalProperties['callcenter.dtmf.timeout']}")
    private String callCenterDtmfTimeout;

    @Value("#{ghanaNationalProperties['callcenter.dtmf.finishonkey']}")
    private String callCenterFinishOnKey;

    public Node play(String motechId, String language) {
        List<OutboundVoiceMessage> audioClips = allPatientsOutbox.getAudioFileNames(motechId);
        List<OutboundVoiceMessage> scheduleClips = Lambda.filter(having(on(OutboundVoiceMessage.class).getParameters(), not(hasEntry(TYPE, MOBILE_MIDWIFE.name()))), audioClips);
        List<OutboundVoiceMessage> mmClips = Lambda.filter(having(on(OutboundVoiceMessage.class).getParameters(), hasEntry(TYPE, MOBILE_MIDWIFE.name())), audioClips);

        Node node = new Node();
        List<String> scheduleClipNames = getClipNames(scheduleClips);
        List<String> mmClipNames = getClipNames(mmClips);

        if (isEmpty(audioClips))
            return node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(NO_MESSAGE_IN_OUTBOX.value(), valueOf(language))));

        for (String scheduleClipName : scheduleClipNames) {
            node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(scheduleClipName, valueOf(language))));
        }

        for (String mmClipName : mmClipNames) {
            MobileMidwifeAudioClips mobileMidwifeAudioClips = MobileMidwifeAudioClips.valueOf(mmClipName);
            playMultipleMMClips(mobileMidwifeAudioClips.getClipNames(), mobileMidwifeAudioClips.getPromptClipNames(), node, null, ivrClipManager, language);
        }
         return node;
    }

    private void playMultipleMMClips(List<String> pendingClips, List<String> pendingPrompts, Node node, Node previousNode, IVRClipManager ivrClipManager, String language) {

        if (CollectionUtils.isEmpty(pendingClips))
            return;

        Node rootNode = new Node();

        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("1", new Transition().setDestinationNode(rootNode));

        for (int i = 1; i < pendingClips.size(); i++) {
            Node transitionNode = new Node();
            playMultipleMMClips(pendingClips.subList(i, pendingClips.size()), pendingPrompts.subList(i, pendingPrompts.size()), transitionNode, node, ivrClipManager, language);
            transitions.put(i + 1 + "", new Transition().setDestinationNode(transitionNode));
        }
        if (pendingClips.size() == 1 && previousNode != null) {
            transitions.put("2", new Transition().setDestinationNode(previousNode));
        }

        rootNode.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(pendingClips.get(0), valueOf(language))))
                .addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(pendingPrompts.get(0), valueOf(language))))
                .setTransitions(transitions);
        rootNode.setTransitionTimeout(callCenterDtmfTimeout);
        rootNode.setTransitionFinishOnKey(callCenterFinishOnKey);

        if (pendingClips.size() == 1)
            rootNode.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(AudioPrompts.GHS.value(), valueOf(language))));

        for (Prompt prompt : rootNode.getPrompts()) {
            node.addPrompts(prompt);
        }
        node.setTransitionTimeout(callCenterDtmfTimeout);
        node.setTransitionFinishOnKey(callCenterFinishOnKey);
        node.setTransitions(transitions);
    }

    private List<String> getClipNames(List<OutboundVoiceMessage> clips) {
        List<String> clipNames = new ArrayList<String>();
        for (OutboundVoiceMessage clip : clips) {
            clipNames.add((String) clip.getParameters().get(AUDIO_CLIP_NAME));
        }
        return clipNames;
    }
}
