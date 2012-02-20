package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

public class PregnancyTest extends BaseUnitTest {

    @Test
    public void shouldCreateIfExpectedDateOfDeliveryIsWithin40Weeks() {
        LocalDate todayAs1Jan2012 = new LocalDate(2012, 1, 1);
        mockCurrentDate(todayAs1Jan2012);
        basedOnDeliveryDate(todayAs1Jan2012.plusWeeks(40));
        try {
            basedOnDeliveryDate(todayAs1Jan2012.plusWeeks(40).plusDays(1));
            Assert.fail("should throw exception");
        } catch(IllegalArgumentException iae ) {
        }
    }

    @Test
    public void shouldFindCurrentWeekFromExpectedDeliveryDate() {

        LocalDate todayAs26Jun2012 = new LocalDate(2012, 6, 27);
        mockCurrentDate(todayAs26Jun2012);

        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusDays(0)).currentWeek(), is(equalTo(40)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusDays(7)).currentWeek(), is(equalTo(39)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(40)).currentWeek(), is(equalTo(0)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(22)).currentWeek(), is(equalTo(18)));

        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(28)).currentWeek(), is(equalTo(12)));
        assertThat(basedOnDeliveryDate(todayAs26Jun2012.plusWeeks(26).plusDays(7)).currentWeek(), is(equalTo(13)));
    }
}
