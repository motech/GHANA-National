package org.motechproject.ghana.national.tools;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.group.Group;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Facility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
}
