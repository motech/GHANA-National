package org.motechproject.ghana.national.tools.seed.data;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PNCMotherVaccineSeedTest {

    private PNCMotherVaccineSeed pncMotherVaccineSeed;

    @Before
    public void setUp() throws Exception {
        pncMotherVaccineSeed = new PNCMotherVaccineSeed(null, null, null);
    }

    @Test
    public void shouldReturnScheduleNamesBasedOnMilestoneNames(){
        assertThat(pncMotherVaccineSeed.getScheduleName("PNC1"), is(equalTo(ScheduleNames.PNC_MOTHER_1.getName())));
        assertThat(pncMotherVaccineSeed.getScheduleName("PNC2"), is(equalTo(ScheduleNames.PNC_MOTHER_2.getName())));
        assertThat(pncMotherVaccineSeed.getScheduleName("PNC3"), is(equalTo(ScheduleNames.PNC_MOTHER_3.getName())));
        assertThat(pncMotherVaccineSeed.getScheduleName("PNC-M1"), is(equalTo(ScheduleNames.PNC_MOTHER_1.getName())));
        assertThat(pncMotherVaccineSeed.getScheduleName("PNC-M2"), is(equalTo(ScheduleNames.PNC_MOTHER_2.getName())));
        assertThat(pncMotherVaccineSeed.getScheduleName("PNC-M3"), is(equalTo(ScheduleNames.PNC_MOTHER_3.getName())));
    }

    @Test
    public void shouldMapMilestoneNames(){
        assertThat(pncMotherVaccineSeed.mapMilestoneName("PNC1"), is(equalTo("PNC-M1")));
        assertThat(pncMotherVaccineSeed.mapMilestoneName("PNC2"), is(equalTo("PNC-M2")));
        assertThat(pncMotherVaccineSeed.mapMilestoneName("PNC3"), is(equalTo("PNC-M3")));
    }
}
