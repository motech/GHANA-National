package org.motechproject.ghana.national.web.domain;

import org.quartz.SimpleTrigger;

public class JobDetail {

    SimpleTrigger trigger;
    org.quartz.JobDetail jobDetail;

    public JobDetail(SimpleTrigger trigger, org.quartz.JobDetail jobDetail) {
        this.trigger = trigger;
        this.jobDetail = jobDetail;
    }

    public org.quartz.JobDetail getJobDetail() {
        return jobDetail;
    }

    public SimpleTrigger trigger() {
        return trigger;
    }
}
