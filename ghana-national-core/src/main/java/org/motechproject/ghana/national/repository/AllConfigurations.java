package org.motechproject.ghana.national.repository;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllConfigurations extends MotechBaseRepository<Configuration> {


    @Autowired
    public AllConfigurations(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(Configuration.class, db);
    }

    @View(name = "find_value_by_configuration_name", map = "function(doc) { if(doc.type === 'Configuration') emit(doc.propertyName, doc) }")
    public Configuration getConfigurationValue(String name) {
        ViewQuery viewQuery = createQuery("find_value_by_configuration_name").key(name);
        List<Configuration> configurations = db.queryView(viewQuery, Configuration.class);
        return CollectionUtils.isEmpty(configurations) ? null : configurations.get(0);
    }

}

