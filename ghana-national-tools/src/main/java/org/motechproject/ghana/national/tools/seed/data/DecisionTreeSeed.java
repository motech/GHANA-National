package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component("decisionTreeSeed")
public class DecisionTreeSeed extends Seed {
    @Autowired
    AllTrees allTrees;

    @Override
    protected void load() {
        allTrees.removeAll();

        Tree tree = new Tree();
        tree.setName("mm");
        HashMap<String, Transition> stepOne = new HashMap<String, Transition>();
        HashMap<String, Transition> stepTwo = new HashMap<String, Transition>();
        stepTwo.put("1234567", new Transition().setDestinationNode(new Node()));
        stepOne.put("1", new Transition().setDestinationNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("You have chosen 1. Please enter your motech id:")).setTransitions(stepTwo)));
        stepOne.put("2", new Transition().setDestinationNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("You have chosen 2. Please enter your motech id:")).setTransitions(stepTwo)));
        stepOne.put("3", new Transition().setDestinationNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("You have chosen 3. Please enter your motech id:")).setTransitions(stepTwo)));
        stepOne.put("4", new Transition().setDestinationNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("You have chosen 4. Please enter your motech id:")).setTransitions(stepTwo)));
        tree.setRootNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("Hello welcome to MoTeCH. Press One for English, Two for Fanti!")).setTransitions(stepOne));

        allTrees.add(tree);
    }
}
