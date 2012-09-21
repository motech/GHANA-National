package org.motechproject.ghana.national.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class MobileMidwifeWeekCalculatorTest extends BaseUnitTest {


    @Autowired
    MobileMidwifeWeekCalculator mobileMidwifeWeekCalculator;

    @Test
    public void shouldDetermineIfProgramIsEnded() {
        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_VOICE", "40"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_VOICE", "35"));

        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_SMS", "PREGNANCY-cw41-Friday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_SMS", "PREGNANCY-cw40-Monday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("PREGNANCY_SMS", "PREGNANCY-cw30-Friday"));

        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_VOICE", "52"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_VOICE", "5"));

        assertTrue(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_SMS", "CHILD_CARE-cw52-Monday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_SMS", "CHILD_CARE-cw50-Friday"));
        assertFalse(mobileMidwifeWeekCalculator.hasProgramEnded("CHILD_CARE_SMS", "CHILD_CARE-cw40-Friday"));
    }

}
