package org.motechproject.ghana.national.messagegateway.domain;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.store.MessageGroupStoreReaper;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class MessageGroupStoreRepearJob extends QuartzJobBean {
    private Logger logger = LoggerFactory.getLogger(MessageGroupStoreRepearJob.class);

    private int timeout;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final ApplicationContext applicationContext;
        try {
            applicationContext = (ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContext");
            ((MessageGroupStoreReaper) applicationContext.getBean("reaper")).run();

        } catch (SchedulerException e) {
            logger.error("Encountered exception while executing reaper job", e);
        }
    }

}
