package org.motechproject.ghana.national.schedule;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 *
 */
@Component
public class FacilitiesDefaultMessageScheduler {

    @Autowired
    MotechSchedulerService schedulerService;

    @Value("#{ghanaNationalProperties['facility.default.message.cron']}")
    private String defaultMessageCronExpression;
    static String JOB_ID_KEY = "default-facility-message";

    @PostConstruct
    public void init() {
        MotechEvent motechEvent = new MotechEvent(Constants.FACILITIES_DEFAULT_MESSAGE_SUBJECT, new HashMap<String, Object>() {{
            put(MotechSchedulerService.JOB_ID_KEY, JOB_ID_KEY);
        }});
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, defaultMessageCronExpression);
        schedulerService.scheduleJob(cronSchedulableJob);
    }
}
