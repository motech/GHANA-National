package org.motechproject.ghana.national.vo;

import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.testing.utils.BaseUnitTest;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.motechproject.util.DateUtil.newDateTime;

public class ChildCareTest extends BaseUnitTest {

    @Test
    public void shouldBeApplicableForMeaslesIfChildBirthIsWithinOneYear() {

        DateTime birthDate = newDateTime(2012, 1, 4, 10, 10, 0);

        mockCurrentDate(birthDate.plusYears(5).minusDays(1));
        assertTrue(ChildCare.basedOnBirthDay(birthDate).applicableForMeasles());

        mockCurrentDate(birthDate.plusYears(5));
        assertFalse(ChildCare.basedOnBirthDay(birthDate).applicableForMeasles());
    }
    
    @Test
    public void shouldVerifyIfApplicableForIPT() {

        DateTime birthDate = newDateTime(2012, 1, 4, 10, 12, 0);

        assertIfChildIsApplicableForIPT(birthDate, birthDate, is(true));
        assertIfChildIsApplicableForIPT(birthDate.plusWeeks(1), birthDate, is(true));
        assertIfChildIsApplicableForIPT(birthDate.plusWeeks(13).plusDays(6), birthDate, is(true));
        assertIfChildIsApplicableForIPT(birthDate.plusWeeks(14), birthDate, is(false));
        assertIfChildIsApplicableForIPT(birthDate.plusWeeks(-1), birthDate, is(false));
    }

    private void assertIfChildIsApplicableForIPT(DateTime today, DateTime birthDate, Matcher<Boolean> expected) {
        mockCurrentDate(today);
        assertThat(ChildCare.basedOnBirthDay(birthDate).applicableForIPTi(), expected);
    }
}
