package org.motechproject.ghana.national.domain.mobilemidwife;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MessageStartWeekTest {
    @Test
    public void shouldReturnListOfMessageStartWeeks() {
        ArrayList<MessageStartWeek> expectedMessageStartWeeks = new ArrayList<MessageStartWeek>() {{
            add(new MessageStartWeek("5", 5, "Pregnancy-week 5", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("6", 6, "Pregnancy-week 6", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("7", 7, "Pregnancy-week 7", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("8", 8, "Pregnancy-week 8", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("9", 9, "Pregnancy-week 9", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("10", 10, "Pregnancy-week 10", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("11", 11, "Pregnancy-week 11", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("12", 12, "Pregnancy-week 12", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("13", 13, "Pregnancy-week 13", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("14", 14, "Pregnancy-week 14", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("15", 15, "Pregnancy-week 15", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("16", 16, "Pregnancy-week 16", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("17", 17, "Pregnancy-week 17", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("18", 18, "Pregnancy-week 18", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("19", 19, "Pregnancy-week 19", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("20", 20, "Pregnancy-week 20", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("21", 21, "Pregnancy-week 21", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("22", 22, "Pregnancy-week 22", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("23", 23, "Pregnancy-week 23", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("24", 24, "Pregnancy-week 24", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("25", 25, "Pregnancy-week 25", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("26", 26, "Pregnancy-week 26", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("27", 27, "Pregnancy-week 27", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("28", 28, "Pregnancy-week 28", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("29", 29, "Pregnancy-week 29", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("30", 30, "Pregnancy-week 30", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("31", 31, "Pregnancy-week 31", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("32", 32, "Pregnancy-week 32", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("33", 33, "Pregnancy-week 33", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("34", 34, "Pregnancy-week 34", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("35", 35, "Pregnancy-week 35", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("36", 36, "Pregnancy-week 36", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("37", 37, "Pregnancy-week 37", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("38", 38, "Pregnancy-week 38", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("39", 39, "Pregnancy-week 39", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("40", 40, "Pregnancy-week 40", ServiceType.PREGNANCY_TEXT));
            add(new MessageStartWeek("41", 1, "Baby-week 1", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("42", 2, "Baby-week 2", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("43", 3, "Baby-week 3", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("44", 4, "Baby-week 4", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("45", 5, "Baby-week 5", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("46", 6, "Baby-week 6", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("47", 7, "Baby-week 7", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("48", 8, "Baby-week 8", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("49", 9, "Baby-week 9", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("50", 10, "Baby-week 10", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("51", 11, "Baby-week 11", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("52", 12, "Baby-week 12", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("53", 13, "Baby-week 13", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("54", 14, "Baby-week 14", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("55", 15, "Baby-week 15", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("56", 16, "Baby-week 16", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("57", 17, "Baby-week 17", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("58", 18, "Baby-week 18", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("59", 19, "Baby-week 19", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("60", 20, "Baby-week 20", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("61", 21, "Baby-week 21", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("62", 22, "Baby-week 22", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("63", 23, "Baby-week 23", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("64", 24, "Baby-week 24", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("65", 25, "Baby-week 25", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("66", 26, "Baby-week 26", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("67", 27, "Baby-week 27", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("68", 28, "Baby-week 28", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("69", 29, "Baby-week 29", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("70", 30, "Baby-week 30", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("71", 31, "Baby-week 31", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("72", 32, "Baby-week 32", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("73", 33, "Baby-week 33", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("74", 34, "Baby-week 34", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("75", 35, "Baby-week 35", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("76", 36, "Baby-week 36", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("77", 37, "Baby-week 37", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("78", 38, "Baby-week 38", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("79", 39, "Baby-week 39", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("80", 40, "Baby-week 40", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("81", 41, "Baby-week 41", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("82", 42, "Baby-week 42", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("83", 43, "Baby-week 43", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("84", 44, "Baby-week 44", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("85", 45, "Baby-week 45", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("86", 46, "Baby-week 46", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("87", 47, "Baby-week 47", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("88", 48, "Baby-week 48", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("89", 49, "Baby-week 49", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("90", 50, "Baby-week 50", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("91", 51, "Baby-week 51", ServiceType.CHILD_CARE_TEXT));
            add(new MessageStartWeek("92", 52, "Baby-week 52", ServiceType.CHILD_CARE_TEXT));
        }};
                
        for(MessageStartWeek startWeek : expectedMessageStartWeeks) {
            assertThat(MessageStartWeek.findBy(startWeek.getKey()), is(equalTo(startWeek)));
        }
        assertThat(MessageStartWeek.messageStartWeeks(), is(equalTo(expectedMessageStartWeeks)));
    }
}
