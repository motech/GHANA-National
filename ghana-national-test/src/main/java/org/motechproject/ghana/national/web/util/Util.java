package org.motechproject.ghana.national.web.util;

public class Util {
    public static String getPhoneNumber(int uniqueId) {
        final int paddingLength = 9 - String.valueOf(uniqueId).length();
        return String.valueOf(String.format("0%0" + paddingLength + "d%d", 0, uniqueId));
    }
}
