package org.motechproject.ghana.national.configuration;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;

public class TestJobDetail {

    SimpleTrigger trigger;
    JobDetail jobDetail;

    public TestJobDetail(SimpleTrigger trigger, JobDetail jobDetail) {
        this.trigger = trigger;
        this.jobDetail = jobDetail;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public SimpleTrigger trigger() {
        return trigger;
    }
}
