package org.motechproject.ghana.national.tools;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.group.Group;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.model.DayOfWeek;

import java.util.*;

import static java.util.Arrays.*;
import static org.motechproject.model.DayOfWeek.getDayOfWeek;

public class Utility {
    public static Converter<String, Set<String>> mapConverter(final Group<Facility> facilityGroup) {
        return new Converter<String, Set<String>>() {
            @Override
            public Set<String> convert(String s) {
                return facilityGroup.findGroup(s).keySet();
            }
        };
    }

    public static <K, V> HashMap<V, K> reverseKeyValues(Map<K, V> map) {
        HashMap<V, K> reversed = new HashMap<V, K>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            reversed.put(entry.getValue(), entry.getKey());
        }
        return reversed;
    }

    public static String safeToString(Object object) {
        return object == null ? null : object.toString();
    }

    public static String emptyToNull(String string) {
        return (StringUtils.isEmpty(string.trim())) ? null : string;
    }

    public static Integer safePareInteger(String string) {
        return (string == null) ? null : Integer.parseInt(string);
    }

    public static <V> V nullSafe(V value, V defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> List<T> nullSafeList(T... items) {
        return nullSafeList(asList(items));
    }

    public static <T> List<T> nullSafeList(List<T> items) {
        List<T> result = new ArrayList<T>();
        for(T item : items) {
            if(item != null) result.add(item);
        }
        return result;
    }

    //TODO :Already in platform, but problem in finding method
    public static DateTime nextApplicableWeekDay(DateTime fromDate, List<DayOfWeek> applicableDays) {
        fromDate = fromDate.dayOfMonth().addToCopy(1);
        int dayOfWeek = fromDate.getDayOfWeek();
        int noOfDaysToNearestCycleDate = 0;
        int WEEK_MAX_DAY = DayOfWeek.Sunday.getValue();
        for (int currentDayOfWeek = dayOfWeek, dayCount = 0; dayCount <= WEEK_MAX_DAY; dayCount++) {
            if (applicableDays.contains(getDayOfWeek(currentDayOfWeek))) {
                noOfDaysToNearestCycleDate = dayCount;
                break;
            }
            if (currentDayOfWeek == WEEK_MAX_DAY) currentDayOfWeek = 1;
            else currentDayOfWeek++;
        }
        return fromDate.dayOfMonth().addToCopy(noOfDaysToNearestCycleDate);
    }

}
