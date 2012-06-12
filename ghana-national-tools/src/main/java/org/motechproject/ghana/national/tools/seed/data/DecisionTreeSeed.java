package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.MotechIdValidationTransition;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component("decisionTreeSeed")
public class DecisionTreeSeed extends Seed {
    public static final String NINE_DIGITS = "000000000";
    @Autowired
    AllTrees allTrees;
    @Autowired
    private IVRClipManager ivrClipManager;

    @Override
    protected void load() {
        allTrees.removeAll();

        HashMap<String, ITransition> languageTransition = new HashMap<String, ITransition>();
        //TODO: Should redirect to another app/channel
        Transition customerCareTransition = new Transition().setDestinationNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("Redirecting to Customer Care")));
        HashMap<String, ITransition> motechIdTransitions = new HashMap<String, ITransition>();
        motechIdTransitions.put(NINE_DIGITS, new Transition().setDestinationNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("Redirecting to Customer"))));

        final Language[] languages = Language.values();
        for (int count = 1; count <= languages.length; count++) {
            final Language language = languages[count - 1];
            HashMap<String, ITransition> welcomeMessageTransitions = new HashMap<String, ITransition>();
            welcomeMessageTransitions.put("0", customerCareTransition);
            welcomeMessageTransitions.put("1", new Transition().setDestinationNode(new Node().addPrompts(audioPromptFor(AudioPrompts.MOTECHID_PROMPT, language)).setTransitions(motechIdTransitions)));
            welcomeMessageTransitions.put("2", customerCareTransition);

            languageTransition.put(String.valueOf(count), new Transition().setDestinationNode(new Node().addPrompts(audioPromptFor(AudioPrompts.LANGUAGE_PROMPT, language)).setTransitions(welcomeMessageTransitions)));
        }

        languageTransition.put("*", customerCareTransition);
        Tree tree = new Tree();
        tree.setName("mm");
        tree.setRootNode(new Node().addPrompts(audioPromptFor(AudioPrompts.WELCOME_PROMPT, Language.EN)).setTransitions(languageTransition));

        allTrees.addOrReplace(tree);
    }

    private AudioPrompt audioPromptFor(AudioPrompts prompt, Language language) {
        return new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(prompt.value(), language));
    }
}
