package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITransition;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.TextToSpeechPrompt;

public class CustomTransition implements ITransition {
    @Override
    public Node getDestinationNode(String s) {
        return new Node().setPrompts(new TextToSpeechPrompt().setMessage("custom transition "),
                new AudioPrompt().setAudioFileUrl("custom.wav"));
    }
}
