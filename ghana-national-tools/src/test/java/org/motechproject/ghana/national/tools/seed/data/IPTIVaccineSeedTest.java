package org.motechproject.ghana.national.tools.seed.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.repository.AllCareSchedules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

public class IPTIVaccineSeedTest {

    @Mock
    private AllCareSchedules allCareSchedules;
    private IPTIVaccineSeed iptiVaccineSeed;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        iptiVaccineSeed = new IPTIVaccineSeed(null, null, allCareSchedules);
    }

    @Test
    public void shouldRemoveExtraCharacterFromMilestoneName(){
        assertThat(new IPTIVaccineSeed(null, null, null).mapMilestoneName("IPTi1"), is(equalTo("IPTi1")));
    }
}
