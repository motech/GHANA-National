package org.motechproject.ghana.national.tools.seed.data;

import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OPVSeedTest {
    @Test
    public void shouldReturnScheduleNameBasedOnMilestoneName(){
        assertThat(new OPVSeed(null, null, null).getScheduleName("OPV0"), is(equalTo(ScheduleNames.CWC_OPV_0)));
        assertThat(new OPVSeed(null, null, null).getScheduleName("OPV1"), is(equalTo(ScheduleNames.CWC_OPV_OTHERS)));
        assertThat(new OPVSeed(null, null, null).getScheduleName("OPV2"), is(equalTo(ScheduleNames.CWC_OPV_OTHERS)));
        assertThat(new OPVSeed(null, null, null).getScheduleName("OPV3"), is(equalTo(ScheduleNames.CWC_OPV_OTHERS)));
    }
}
