package org.motechproject.ghana.national.schedule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class FacilitiesDefaultMessageSchedulerTest {

    @Mock
    MotechSchedulerService mockSchedulerService;

    @Autowired
    FacilitiesDefaultMessageScheduler scheduler;

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(scheduler, "schedulerService", mockSchedulerService);
    }

    @Test
    public void shouldCreateACronJobToSendDefaultMessageWeekly() {
        scheduler.init();
        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(mockSchedulerService).scheduleJob(captor.capture());
        CronSchedulableJob actualCronJob = captor.getValue();
        assertThat(actualCronJob.getMotechEvent().getSubject(), is(Constants.FACILITIES_DEFAULT_MESSAGE_SUBJECT));
        assertThat((String) actualCronJob.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY),
                is(FacilitiesDefaultMessageScheduler.JOB_ID_KEY));
        assertThat(actualCronJob.getCronExpression(), is("0 0 8 ? * SUN"));
    }
}
