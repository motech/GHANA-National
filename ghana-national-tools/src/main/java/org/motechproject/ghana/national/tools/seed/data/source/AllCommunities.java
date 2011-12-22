package org.motechproject.ghana.national.tools.seed.data.source;

import org.codehaus.jackson.JsonNode;
import org.ektorp.*;
import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.ChangesFeed;
import org.ektorp.changes.DocumentChange;
import org.ektorp.http.HttpClient;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

@Repository
public class AllCommunities extends MotechAuditableRepository<Community>{

    @Autowired
    public AllCommunities(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(Community.class,db );
    }

    @Override
    public void add(Community community) {
        super.add(community);
    }
}
