package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.testing.utils.BaseUnitTest;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

public class PregnancyTest extends BaseUnitTest {
    private LocalDate todayAs26Jun2012;

    @Before
    public void setUp() {
        todayAs26Jun2012 = new LocalDate(2012, 6, 27);
        mockCurrentDate(todayAs26Jun2012);
    }

    @Test
    public void shouldFindCurrentWeekFromExpectedDeliveryDate() {

        LocalDate todayAs26Jun2012 = new LocalDate(2012, 12, 27);
        mockCurrentDate(todayAs26Jun2012);

        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(39).plusDays(2)).currentWeek(), is(equalTo(1)));
        // edd = conception_Date + 40 weeks will fall in 41 week
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusDays(0)).currentWeek(), is(equalTo(40 + 1)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusDays(7)).currentWeek(), is(equalTo(39 + 1)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(40)).currentWeek(), is(equalTo(0 + 1)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(22)).currentWeek(), is(equalTo(18 + 1)));

        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(28)).currentWeek(), is(equalTo(12 + 1)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(26).plusDays(7)).currentWeek(), is(equalTo(13 +1)));
    }

    @Test
    public void shouldVerifyIsApplicableForIPT() {
        assertTrue(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(28)).applicableForIPT());
        assertFalse(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(22)).applicableForIPT());
    }
}
