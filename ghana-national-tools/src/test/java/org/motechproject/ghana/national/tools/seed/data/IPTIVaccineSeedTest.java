package org.motechproject.ghana.national.tools.seed.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.repository.AllSchedules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

public class IPTIVaccineSeedTest {

    @Mock
    private AllSchedules allSchedules;
    private IPTIVaccineSeed iptiVaccineSeed;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        iptiVaccineSeed = new IPTIVaccineSeed(null, null, allSchedules);
    }

    @Test
    public void shouldRemoveExtraCharacterFromMilestoneName(){
        assertThat(new IPTIVaccineSeed(null, null, null).mapMilestoneName("IPTI1"), is(equalTo("IPT1")));
    }
}
