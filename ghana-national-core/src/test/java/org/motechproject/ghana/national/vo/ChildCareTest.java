package org.motechproject.ghana.national.vo;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.testing.utils.BaseUnitTest;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.util.DateUtil.newDateTime;

public class ChildCareTest extends BaseUnitTest {

    @Test
    public void shouldBeApplicableForMeaslesIfChildBirthIsWithinOneYear() {

        DateTime birthDate = newDateTime(2012, 1, 4, 10, 10, 0);

        mockCurrentDate(birthDate.plusYears(5).minusDays(1));
        assertTrue(new ChildCare(birthDate.toLocalDate()).applicableForMeasles());

        mockCurrentDate(birthDate.plusYears(5));
        assertFalse(new ChildCare(birthDate.toLocalDate()).applicableForMeasles());
    }
}
