package org.motechproject.ghana.national.domain.ivr;

import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.motechproject.MotechException;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.*;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.retry.service.RetryService;
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

    @Autowired
    RetryService retryService;

    private ConnectToCallCenter connectToCallCenter = new ConnectToCallCenter();


    @Value("#{ghanaNationalProperties['callcenter.number']}")
    private String callCenterPhoneNumber;

    @Value("#{ghanaNationalProperties['callcenter.dtmf.timeout']}")
    private String callCenterDtmfTimeout;

    @Value("#{ghanaNationalProperties['callcenter.dtmf.finishonkey']}")
    private String callCenterFinishOnKey;

    public Node play(final String motechId, final String language) {
        List<OutboundVoiceMessage> audioClips = allPatientsOutbox.getAudioFileNames(motechId);
        List<OutboundVoiceMessage> scheduleClips = Lambda.filter(having(on(OutboundVoiceMessage.class).getParameters(), not(hasEntry(TYPE, MOBILE_MIDWIFE.name()))), audioClips);
        List<OutboundVoiceMessage> mmClips = Lambda.filter(having(on(OutboundVoiceMessage.class).getParameters(), hasEntry(TYPE, MOBILE_MIDWIFE.name())), audioClips);

        final Node node = new Node();
        List<String> scheduleClipNames = getClipNames(scheduleClips);
        final List<String> mmClipNames = getClipNames(mmClips);

        if (isEmpty(audioClips))
            return node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(NO_MESSAGE_IN_OUTBOX.value(), valueOf(language))));

        for (String scheduleClipName : scheduleClipNames) {
            node.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(scheduleClipName, valueOf(language))));
        }

        final Node mmNode = new Node();
        for (String mmClipName : mmClipNames) {
            MobileMidwifeAudioClips mobileMidwifeAudioClips = MobileMidwifeAudioClips.valueOf(mmClipName);
            playMultipleMMClips(mobileMidwifeAudioClips.getClipNames(), mobileMidwifeAudioClips.getPromptClipNames(), mmNode, ivrClipManager, language);
            lastMMClipShouldHaveAnOptionToPlayThePreviousOne(mmNode, mmNode, getLastMMPrompts(mmNode));
        }
        node.addPrompts(mmNode.getPrompts().get(0));

        node.addTransition("timeout", new Transition() {
            @Override
            public Node getDestinationNode(String input, FlowSession session) {
                retryService.fulfill(motechId, Constants.RETRY_GROUP);
                Node node = new Node();
                cloneNodeWithoutPrompts(mmNode, node);
                node.addPrompts(mmNode.getPrompts().get(1));
                return node;
            }
        });
        node.setTransitionTimeout("0");
        return node;
    }

    private void cloneNodeWithoutPrompts(Node sourceNode, Node destinationNode) {
        destinationNode.setTransitionPrompts(sourceNode.getTransitionPrompts());
        destinationNode.setTransitionFinishOnKey(sourceNode.getTransitionFinishOnKey());
        destinationNode.setTransitionNumDigits(sourceNode.getTransitionNumDigits());
        destinationNode.setTransitionTimeout(sourceNode.getTransitionTimeout());
        destinationNode.setTransitions(sourceNode.getTransitions());
    }

    private List<Prompt> getLastMMPrompts(Node node) {
        Integer lastClipTransitionKey = null;
        for (String key : node.getTransitions().keySet()) {
            Integer keyAsInt = Utility.stringToInteger(key);
            if (keyAsInt != null) {
                if (lastClipTransitionKey == null || keyAsInt > lastClipTransitionKey)
                    lastClipTransitionKey = keyAsInt;
            }
        }
        return ((Transition) node.getTransitions().get(lastClipTransitionKey.toString())).getDestinationNode().getPrompts();
    }

    private void lastMMClipShouldHaveAnOptionToPlayThePreviousOne(Node node, Node rootNode, List<Prompt> lastMMPromptsOfRootNode) {
        Integer repeatKey = 1;
        for (String key : node.getTransitions().keySet()) {
            Integer keyAsInt = Utility.stringToInteger(key);
            if (keyAsInt != null && !repeatKey.equals(keyAsInt)) {
                lastMMClipShouldHaveAnOptionToPlayThePreviousOne(getDestinationNode(node.getTransitions().get(key)), rootNode, lastMMPromptsOfRootNode);
                if (((Transition) node.getTransitions().get(key)).getDestinationNode().getPrompts().equals(lastMMPromptsOfRootNode)) {
                    ((Transition) node.getTransitions().get(key)).getDestinationNode().getTransitions().put("2", rootNode.getTransitions().get("2"));
                }
            }
        }

    }

    private Node getDestinationNode(ITransition transition) {
        if (transition instanceof Transition) {
            return ((Transition) transition).getDestinationNode();
        } else {
            throw new MotechException("This should not happen");
        }
    }

    private void playMultipleMMClips(List<String> pendingClips, List<String> pendingPrompts, Node node, IVRClipManager ivrClipManager, String language) {

        if (CollectionUtils.isEmpty(pendingClips))
            return;

        Node rootNode = new Node();

        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("1", new Transition().setDestinationNode(rootNode));

        for (int i = 1; i < pendingClips.size(); i++) {
            Node transitionNode = new Node();
            playMultipleMMClips(pendingClips.subList(i, pendingClips.size()), pendingPrompts.subList(i, pendingPrompts.size()), transitionNode, ivrClipManager, language);
            transitions.put(i + 1 + "", new Transition().setDestinationNode(transitionNode));
        }
        transitions.put("timeout", new Transition().setDestinationNode(new Node()));
        transitions.put("?", connectToCallCenter.getAsTransition(callCenterPhoneNumber));


        rootNode.addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(pendingClips.get(0), valueOf(language))))
                .addPrompts(new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(pendingPrompts.get(0), valueOf(language))))
                .setTransitions(transitions);
        rootNode.setTransitionNumDigits("1");
        rootNode.setTransitionTimeout(callCenterDtmfTimeout);
        rootNode.setTransitionFinishOnKey(callCenterFinishOnKey);

        for (Prompt prompt : rootNode.getPrompts()) {
            node.addPrompts(prompt);
        }
        node.setTransitionNumDigits("1");
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
