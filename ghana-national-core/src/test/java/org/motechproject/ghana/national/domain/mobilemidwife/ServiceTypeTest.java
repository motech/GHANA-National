package org.motechproject.ghana.national.domain.mobilemidwife;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServiceTypeTest {

    @Test
    public void shouldReturnListOfMessageStartWeeksForChildCare() {
        Map<String, String> expectedMessageStartWeeks = new HashMap<String, String>() {{
            put("41", "Baby-week 1");put("42", "Baby-week 2");put("43", "Baby-week 3");put("44", "Baby-week 4");
            put("45", "Baby-week 5");put("46", "Baby-week 6");put("47", "Baby-week 7");put("48", "Baby-week 8");
            put("49", "Baby-week 9");put("50", "Baby-week 10");put("51", "Baby-week 11");put("52", "Baby-week 12");
            put("53", "Baby-week 13");put("54", "Baby-week 14");put("55", "Baby-week 15");put("56", "Baby-week 16");
            put("57", "Baby-week 17");put("58", "Baby-week 18");put("59", "Baby-week 19");put("60", "Baby-week 20");
            put("61", "Baby-week 21");put("62", "Baby-week 22");put("63", "Baby-week 23");put("64", "Baby-week 24");
            put("65", "Baby-week 25");put("66", "Baby-week 26");put("67", "Baby-week 27");put("68", "Baby-week 28");
            put("69", "Baby-week 29");put("70", "Baby-week 30");put("71", "Baby-week 31");put("72", "Baby-week 32");
            put("73", "Baby-week 33");put("74", "Baby-week 34");put("75", "Baby-week 35");put("76", "Baby-week 36");
            put("77", "Baby-week 37");put("78", "Baby-week 38");put("79", "Baby-week 39");put("80", "Baby-week 40");
            put("81", "Baby-week 41");put("82", "Baby-week 42");put("83", "Baby-week 43");put("84", "Baby-week 44");
            put("85", "Baby-week 45");put("86", "Baby-week 46");put("87", "Baby-week 47");put("88", "Baby-week 48");
            put("89", "Baby-week 49");put("90", "Baby-week 50");put("91", "Baby-week 51");put("92", "Baby-week 52");
        }};
        assertThat(ServiceType.CHILD_CARE.messageStartWeeks(), is(equalTo(expectedMessageStartWeeks)));
    }

    @Test
    public void shouldReturnListOfMessageStartWeeksForPregnancyCare() {
        Map<String, String> expectedMessageStartWeeks = new HashMap<String, String>() {{
            put("5", "Pregnancy-week 5");put("6", "Pregnancy-week 6");put("7", "Pregnancy-week 7");
            put("8", "Pregnancy-week 8");put("9", "Pregnancy-week 9");put("10", "Pregnancy-week 10");
            put("11", "Pregnancy-week 11");put("12", "Pregnancy-week 12");put("13", "Pregnancy-week 13");
            put("14", "Pregnancy-week 14");put("15", "Pregnancy-week 15");put("16", "Pregnancy-week 16");
            put("17", "Pregnancy-week 17");put("18", "Pregnancy-week 18");put("19", "Pregnancy-week 19");
            put("20", "Pregnancy-week 20");put("21", "Pregnancy-week 21");put("22", "Pregnancy-week 22");
            put("23", "Pregnancy-week 23");put("24", "Pregnancy-week 24");put("25", "Pregnancy-week 25");
            put("26", "Pregnancy-week 26");put("27", "Pregnancy-week 27");put("28", "Pregnancy-week 28");
            put("29", "Pregnancy-week 29");put("30", "Pregnancy-week 30");put("31", "Pregnancy-week 31");
            put("32", "Pregnancy-week 32");put("33", "Pregnancy-week 33");put("34", "Pregnancy-week 34");
            put("35", "Pregnancy-week 35");put("36", "Pregnancy-week 36");put("37", "Pregnancy-week 37");
            put("38", "Pregnancy-week 38");put("39", "Pregnancy-week 39");put("40", "Pregnancy-week 40");
        }};
        assertThat(ServiceType.PREGNANCY.messageStartWeeks(), is(equalTo(expectedMessageStartWeeks)));
    }

}
