package org.motechproject.ghana.national.tools.seed;

import org.slf4j.LoggerFactory;

public abstract class Seed {
    org.slf4j.Logger LOG = LoggerFactory.getLogger(this.getClass());

    public void run() {
        preLoad();
        load();
        postLoad();
    }

    private void postLoad() {
        LOG.info("Seed finished.");
    }

    private void preLoad() {
        LOG.info("Seed started.");
    }

    protected abstract void load();

}