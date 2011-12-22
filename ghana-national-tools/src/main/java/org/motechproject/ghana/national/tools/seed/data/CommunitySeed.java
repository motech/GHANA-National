package org.motechproject.ghana.national.tools.seed.data;


import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.AllCommunities;
import org.motechproject.ghana.national.tools.seed.data.source.Community;
import org.motechproject.ghana.national.tools.seed.data.source.CommunitySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommunitySeed extends Seed {

    @Autowired
    CommunitySource communitySource;
    @Autowired
    AllCommunities allCommunities;

    @Override
    protected void load() {
        try {
            List<Community> communities = communitySource.getAllCommunities();
            for (Community community : communities) {
                allCommunities.add(community);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
