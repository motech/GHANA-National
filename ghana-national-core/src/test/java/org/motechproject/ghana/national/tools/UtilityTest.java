package org.motechproject.ghana.national.tools;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.group.Group;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.model.DayOfWeek;
import org.motechproject.mrs.model.MRSFacility;

import java.util.*;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.motechproject.ghana.national.tools.Utility.nextApplicableWeekDay;

public class UtilityTest {

    @Test
    public void testMapConverter() {
        final String country = "country";
        final String region = "region";
        ArrayList<Facility> facilities = new ArrayList<Facility>() {
            {
                add(new Facility(new MRSFacility("facility", country, region, "district", "state")));
            }
        };
        Group<Facility> facilityGroup = group(facilities, by(on(Facility.class).country()), by(on(Facility.class).region()));
        Converter<String, Set<String>> stringSetConverter = Utility.mapConverter(facilityGroup);
        assertThat(stringSetConverter, is(notNullValue()));

        Set<String> actual = stringSetConverter.convert(country);

        Set<String> expected = new HashSet<String>() {{
            add(region);
        }};
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void testReverseKeyValues() {
        HashMap<String, Integer> actual = Utility.reverseKeyValues(new HashMap<Integer, String>() {{
            put(1, "1");
            put(2, "2");
        }});

        HashMap<String, Integer> expected = new HashMap<String, Integer>() {{
            put("1", 1);
            put("2", 2);
        }};
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldDoToStringSafely() {
        assertNull(Utility.safeToString(null));
        assertEquals("toString", Utility.safeToString(new Object() {
            @Override
            public String toString() {
                return "toString";
            }
        }));
    }

    @Test
    public void shouldReturnNullIfStringIsEmpty() {
        assertNull(Utility.emptyToNull(""));
        assertNull(Utility.emptyToNull(" "));
        assertEquals("Facility", Utility.emptyToNull("Facility"));
    }
    
    @Test
    public void shouldGetNearestCycleDateBasedOnCurrentDayOfWeek() {

        DateTime oct1Sat2011 = new DateTime(2011, 10, 1, 0, 0);
        DateTime oct2Sun2011 = new DateTime(2011, 10, 2, 2, 0);
        DateTime oct3Mon2011 = new DateTime(2011, 10, 3, 0, 0);
        DateTime oct4Tue2011 = new DateTime(2011, 10, 4, 5, 0);
        DateTime oct6Thu2011 = new DateTime(2011, 10, 6, 3, 0);

        List<DayOfWeek> applicableDays = asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday);
        assertThat(nextApplicableWeekDay(oct1Sat2011, applicableDays), is(oct1Sat2011.dayOfYear().addToCopy(2)));
        assertThat(nextApplicableWeekDay(oct2Sun2011, applicableDays), is(oct2Sun2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct3Mon2011, applicableDays), is(oct3Mon2011.dayOfYear().addToCopy(2)));
        assertThat(nextApplicableWeekDay(oct4Tue2011, applicableDays), is(oct4Tue2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct6Thu2011, applicableDays), is(oct6Thu2011.dayOfYear().addToCopy(1)));

        DateTime oct7Fri2011 = new DateTime(2011, 10, 7, 0, 1);
        DateTime oct8Sat2011 = new DateTime(2011, 10, 8, 0, 3);
        DateTime oct9Sun2011 = new DateTime(2011, 10, 9, 0, 4);
        DateTime oct10Mon2011 = new DateTime(2011, 10, 10, 5, 5);

        applicableDays = asList(DayOfWeek.Sunday, DayOfWeek.Saturday);
        assertThat(nextApplicableWeekDay(oct6Thu2011, applicableDays), is(oct6Thu2011.dayOfYear().addToCopy(2)));
        assertThat(nextApplicableWeekDay(oct7Fri2011, applicableDays), is(oct7Fri2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct8Sat2011, applicableDays), is(oct8Sat2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct9Sun2011, applicableDays), is(oct9Sun2011.dayOfYear().addToCopy(6)));
        assertThat(nextApplicableWeekDay(oct10Mon2011, applicableDays), is(oct10Mon2011.dayOfYear().addToCopy(5)));

        DateTime feb25Sat2012 = new DateTime(2012, 2, 25, 0, 5);
        applicableDays = asList(DayOfWeek.Saturday);
        DateTime actualMar3Sat2012 = feb25Sat2012.dayOfYear().addToCopy(7);
        assertNotSame(feb25Sat2012, actualMar3Sat2012);
        assertThat(nextApplicableWeekDay(feb25Sat2012, applicableDays), is(actualMar3Sat2012));
    }
}
