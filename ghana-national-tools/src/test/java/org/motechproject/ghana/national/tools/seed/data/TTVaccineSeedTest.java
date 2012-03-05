package org.motechproject.ghana.national.tools.seed.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.TTVaccineSource;

import java.util.Arrays;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

public class TTVaccineSeedTest {

    @Mock
    private TTVaccineSource ttVaccineSource;

    private TTVaccineSeed ttVaccineSeed;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVaccineSeed = new TTVaccineSeed(ttVaccineSource);

    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionIfAPatientHaveMoreThanOneActiveUpcomingScheduleEntry(){
        List<UpcomingSchedule> upcomingSchedulesFromDb = Arrays.asList(new UpcomingSchedule("100", "2012-12-17 00:00:00", "TT1"), new UpcomingSchedule("100", "2012-12-21 00:00:00", "TT2"), new UpcomingSchedule("101", "2012-12-21 00:00:00", "TT2"));
        ttVaccineSeed.migrate(upcomingSchedulesFromDb);
    }

}
