package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboundDecisionTreeSeed extends Seed {
    @Autowired
    AllTrees allTrees;
    @Autowired
    private IVRClipManager ivrClipManager;

    @Autowired
    private InboundDecisionTreeSeed inboundDecisionTreeSeed;

    @Override
    protected void load() {
        Tree inboundDecisionTree = allTrees.findByName("InboundDecisionTree");
        Tree outboundDecisionTree = new Tree();
        outboundDecisionTree.setName("OutboundDecisionTree");
        outboundDecisionTree.setRootNode(inboundDecisionTree.getRootNode());
        allTrees.addOrReplace(outboundDecisionTree);
    }
}
