package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITransition;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.transition.ConnectToCallCenterTransition;
import org.motechproject.ghana.national.domain.ivr.transition.MotechIdValidationTransition;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.LANGUAGE_PROMPT;
import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.MOTECH_ID_PROMPT;
import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.REASON_FOR_CALL_PROMPT;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.EN;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.FAN;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.KAS;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.NAN;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.GD;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.EWE;

@Component
public class InboundDecisionTreeSeed extends Seed {
    @Value("#{ghanaNationalProperties['callcenter.dtmf.timeout']}")
    private String callCenterDtmfTimeout;

    @Value("#{ghanaNationalProperties['callcenter.dtmf.finishonkey']}")
    private String callCenterFinishOnKey;

    @Value("#{ghanaNationalProperties['callcenter.no.of.digits.in.motech.id']}")
    private String noOfDigitsInMotechId;

    @Autowired
    AllTrees allTrees;

    @Autowired
    private IVRClipManager ivrClipManager;

    @Override
    protected void load() {
        Tree tree = new Tree();
        tree.setName("InboundDecisionTree");
        tree.setRootNode(chooseLanguageNode(3));
        allTrees.addOrReplace(tree);
    }

    private Node chooseLanguageNode(int retry) {
        if (retry != 0) {
            Node node = prompt(LANGUAGE_PROMPT, EN);
            Map<String, ITransition> transitions = new HashMap<String, ITransition>();
            transitions.put("1", new Transition().setDestinationNode(chooseActionNode(EN, 3)));
            transitions.put("2", new Transition().setDestinationNode(chooseActionNode(NAN, 3)));
            transitions.put("3", new Transition().setDestinationNode(chooseActionNode(KAS, 3)));
            transitions.put("4", new Transition().setDestinationNode(chooseActionNode(FAN, 3)));
            transitions.put("5", new Transition().setDestinationNode(chooseActionNode(GD, 3)));
            transitions.put("6", new Transition().setDestinationNode(chooseActionNode(EWE, 3)));
            transitions.put("0", new ConnectToCallCenterTransition(EN));
            transitions.put("*", new ConnectToCallCenterTransition(true));
            transitions.put("?", new Transition().setDestinationNode(chooseLanguageNode(retry - 1)));
            transitions.put("timeout", new ConnectToCallCenterTransition(EN));
            node.setTransitionNumDigits("1");
            node.setTransitionTimeout(callCenterDtmfTimeout);
            node.setTransitions(transitions);
            return node;
        }
        return new Node();
    }

    private Node chooseActionNode(Language language, int retry) {
        if (retry != 0) {
            Node node = prompt(REASON_FOR_CALL_PROMPT, language);
            Map<String, ITransition> transitions = new HashMap<String, ITransition>();
            transitions.put("1", new Transition().setDestinationNode(validateMotechIdNode(language, 3)));
            transitions.put("*", new ConnectToCallCenterTransition(true));
            transitions.put("0", new ConnectToCallCenterTransition(language));
            transitions.put("timeout", new ConnectToCallCenterTransition(language));
            transitions.put("?", new Transition().setDestinationNode(chooseActionNode(language, retry - 1)));
            node.setTransitionNumDigits("1");
            node.setTransitionTimeout(callCenterDtmfTimeout);
            node.setTransitions(transitions);
            return node;
        }
        return new Node();
    }

    private Node validateMotechIdNode(Language language, int pendingRetries) {
        Node node = prompt(MOTECH_ID_PROMPT, language);
        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        MotechIdValidationTransition motechIdValidationTransition = new MotechIdValidationTransition(language.name(), pendingRetries);
        if (pendingRetries != 0) {
            transitions.put("timeout", new Transition().setDestinationNode(validateMotechIdNode(language, pendingRetries - 1)));
            transitions.put("?", motechIdValidationTransition);
        }
        // As requested by david, we expect 7 digits for motech id instead of finish on key
        //  node.setTransitionFinishOnKey(callCenterFinishOnKey);
        node.setTransitionTimeout(callCenterDtmfTimeout);
        node.setTransitionNumDigits(noOfDigitsInMotechId);
        node.setTransitions(transitions);
        return node;
    }

    private Node prompt(AudioPrompts clipName, Language language) {
        return new Node().addTransitionPrompts(audioPromptFor(clipName, language));
    }

    private AudioPrompt audioPromptFor(AudioPrompts prompt, Language language) {
        return new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(prompt.value(), language));
    }
}
