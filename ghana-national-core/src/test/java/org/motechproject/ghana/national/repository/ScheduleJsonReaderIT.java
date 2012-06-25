package org.motechproject.ghana.national.repository;

import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class ScheduleJsonReaderIT {

    @Autowired
    ScheduleJsonReader scheduleJsonReader;

    @Test
    public void shouldReadAlertValidityInformationFromScheduleJsonDefinitionFiles(){
        String scheduleName = "TTVaccine";
        String milestoneName = "TT2";
        String windowName = "due";
        Period validity = scheduleJsonReader.validity(scheduleName, milestoneName, windowName);
        assertThat(validity, is(equalTo(Period.weeks(1))));

        validity = scheduleJsonReader.validity(scheduleName, milestoneName, "late");
        assertThat(validity, is(equalTo(Period.weeks(1))));

        assertNull(scheduleJsonReader.validity("jubk", "junk", "junk"));
        assertNull(scheduleJsonReader.validity(null, null, null));
    }

}
