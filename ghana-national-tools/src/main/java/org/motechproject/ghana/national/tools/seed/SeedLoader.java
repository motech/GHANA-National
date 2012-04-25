package org.motechproject.ghana.national.tools.seed;

import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SeedLoader {
    Logger LOG = LoggerFactory.getLogger(this.getClass());
    private List<Seed> seeds;

    public SeedLoader(List<Seed> seeds) {
        this.seeds = seeds;
    }

    public SeedLoader() {
    }

    @LoginAsAdmin
    @ApiSession
    public void load() {
        LOG.info("Started loading seeds :" + seeds.toString());
        for (Seed seed : seeds) {
            try {
                seed.run();
            } catch (Exception e) {
                LOG.error("Encountered error while loading seed, " + seed.getClass().getName(), e);
            }
        }
    }
}