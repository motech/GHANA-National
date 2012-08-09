package org.motechproject.ghana.national.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.motechproject.util.DateUtil.newDateTime;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class MobileMidwifeWeekCalculatorTest extends BaseUnitTest {


   @Autowired
   MobileMidwifeWeekCalculator mobileMidwifeWeekCalculator;

    @Test
    public void shouldDetermineIfProgramIsEnded() {
        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_VOICE", "40"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_VOICE","35"));

        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_SMS","PREGNANCY-cw41-Friday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_SMS","PREGNANCY-cw40-Monday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_SMS","PREGNANCY-cw30-Friday"));

        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_VOICE","52"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_VOICE","5"));

        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_SMS","CHILD_CARE-cw52-Monday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_SMS","CHILD_CARE-cw50-Friday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_SMS","CHILD_CARE-cw40-Friday"));
    }

    @Test
    public void shouldNotGetMessageKeyIfTriggeredOnANonApplicableDay() {
        mockCurrentDate(newDateTime(2012, 8, 6)); // Monday
        assertThat(mobileMidwifeWeekCalculator.getMessageKey("PREGNANCY_SMS", newDateTime(2012, 8, 6).toLocalDate(), 1, "1 Week"), is(equalTo("PREGNANCY-cw1-Monday")));

        mockCurrentDate(newDateTime(2012, 8, 7)); //Tuesday
        assertThat(mobileMidwifeWeekCalculator.getMessageKey("PREGNANCY_SMS", newDateTime(2012, 8, 6).toLocalDate(), 1, "1 Week"), nullValue());

        mockCurrentDate(newDateTime(2012, 8, 10)); //Friday
        assertThat(mobileMidwifeWeekCalculator.getMessageKey("PREGNANCY_SMS", newDateTime(2012, 8, 6).toLocalDate(), 1, "1 Week"), is(equalTo("PREGNANCY-cw1-Friday")));

        mockCurrentDate(newDateTime(2012, 8, 6)); //Monday
        assertThat(mobileMidwifeWeekCalculator.getMessageKey("CHILD_CARE_SMS", newDateTime(2012, 8, 6).toLocalDate(), 1, "1 Week"), is(equalTo("CHILD_CARE-cw1-Monday")));

        mockCurrentDate(newDateTime(2012, 8, 10)); //Friday
        assertThat(mobileMidwifeWeekCalculator.getMessageKey("CHILD_CARE_SMS", newDateTime(2012, 8, 6).toLocalDate(), 1, "1 Week"), nullValue());
    }
}
