package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.repository.AllOutPatientVisits;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.source.OutPatientVisitSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutPatientVisitMigrationSeed extends Seed {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OutPatientVisitSource outPatientVisitSource;

    @Autowired
    private AllOutPatientVisits allOutPatientVisists;

    @Override
    protected void load() {

        try {
            for (OutPatientVisit outPatientVisist : outPatientVisitSource.getOutPatientVisitList()) {
                allOutPatientVisists.migrateToCouch(outPatientVisist);
            }
        } catch (Exception e) {
            log.info("Exception occurred while migrating OutPatientVisits :" + e);
        }
    }
}
