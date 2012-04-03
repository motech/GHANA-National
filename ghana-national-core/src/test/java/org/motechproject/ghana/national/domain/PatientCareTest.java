package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.motechproject.util.DateUtil.*;

public class PatientCareTest extends BaseUnitTest {
    
    @Test
    public void shouldReturnDefaultPreferredTimeAsNowIfNoReferenceTimeIsSet() {
        Time referenceTime = new Time(2, 2);
        PatientCare careWithReferenceTime = new PatientCare("name", newDateTime(2012, 2, 2, referenceTime), newDateTime(2012, 3, 2, referenceTime), null,null);
        assertNull(careWithReferenceTime.preferredTime());

        DateTime now = DateUtil.newDateTime(2012, 2, 2, 3, 13, 23);
        mockCurrentDate(now);
        PatientCare careWithoutReferenceTime = new PatientCare("name", newDate(2012, 2, 2), null, null, null);
        assertThat(careWithoutReferenceTime.preferredTime(), is(time(now)));
    }
}
