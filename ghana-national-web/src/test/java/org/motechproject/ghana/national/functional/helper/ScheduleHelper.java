package org.motechproject.ghana.national.functional.helper;

import org.joda.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScheduleHelper {
    public static void assertAlertDate(LocalDate actualAlertTime, LocalDate expectedAlertTime) {
        assertThat(actualAlertTime, is(equalTo(expectedAlertTime)));
    }

    public static void assertAlertDate(LocalDate expectedAlertTime, String expectedMilestone, LocalDate actualAlertTime, String actualMilestone) {
        assertThat(actualAlertTime, is(equalTo(expectedAlertTime)));
        assertThat(actualMilestone, is(equalTo(expectedMilestone)));
    }

}
