package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.ValidateMotechIdTransition;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.*;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.*;

@Component
public class InboundDecisionTreeSeed extends Seed {
    @Value("#{ghanaNationalProperties['callcenter.number']}")
    private String callCenterPhoneNumber;

    @Autowired
    AllTrees allTrees;
    @Autowired
    private IVRClipManager ivrClipManager;

    @Override
    protected void load() {
        Tree tree = new Tree();
        tree.setName("InboundDecisionTree");
        tree.setRootNode(prompt(LANGUAGE_PROMPT, EN).setTransitions(chooseLanguageTransitions()));
        allTrees.addOrReplace(tree);
    }

    private Map<String, ITransition> chooseLanguageTransitions() {
        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("1", new Transition().setDestinationNode(prompt(REASON_FOR_CALL_PROMPT, EN).setTransitions(chooseActionTransition(EN))));
        transitions.put("2", new Transition().setDestinationNode(prompt(REASON_FOR_CALL_PROMPT, KAS).setTransitions(chooseActionTransition(KAS))));
        transitions.put("3", new Transition().setDestinationNode(prompt(REASON_FOR_CALL_PROMPT, NAN).setTransitions(chooseActionTransition(NAN))));
        transitions.put("4", new Transition().setDestinationNode(prompt(REASON_FOR_CALL_PROMPT, FAN).setTransitions(chooseActionTransition(FAN))));
        transitions.put("5", customerCareTransition());
        return transitions;
    }

    private Map<String, ITransition> chooseActionTransition(Language language) {
        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("1", new Transition().setDestinationNode(prompt(MOTECH_ID_PROMPT, language).setTransitions(validateMotechIdTransition(language))));
        transitions.put("0", customerCareTransition());
        transitions.put("2", customerCareTransition());
        transitions.put("*", customerCareTransition());
        return transitions;
    }

    private Map<String, ITransition> validateMotechIdTransition(Language language) {
        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        ValidateMotechIdTransition validateMotechIdTransition = new ValidateMotechIdTransition(language.name(), 3);
        transitions.put("?", validateMotechIdTransition);
        return transitions;
    }

    private Node prompt(AudioPrompts clipName, Language language) {
        return new Node().addPrompts(audioPromptFor(clipName, language));
    }


    private Transition customerCareTransition() {
        DialPrompt callCenterDialPrompt = new DialPrompt(callCenterPhoneNumber);
        callCenterDialPrompt.setCallerId(callCenterPhoneNumber);
        return new Transition().setDestinationNode(new Node().addPrompts(callCenterDialPrompt));
    }

    private AudioPrompt audioPromptFor(AudioPrompts prompt, Language language) {
        return new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(prompt.value(), language));
    }
}
