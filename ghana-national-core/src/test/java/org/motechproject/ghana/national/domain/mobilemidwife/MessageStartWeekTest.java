package org.motechproject.ghana.national.domain.mobilemidwife;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MessageStartWeekTest {
    @Test
    public void shouldReturnListOfMessageStartWeeks() {
        List<MessageStartWeek> expectedMessageStartWeeks = new ArrayList<MessageStartWeek>() {{
            add(new MessageStartWeek("5", "Pregnancy-week 5", ServiceType.PREGNANCY));
            add(new MessageStartWeek("6", "Pregnancy-week 6", ServiceType.PREGNANCY));
            add(new MessageStartWeek("7", "Pregnancy-week 7", ServiceType.PREGNANCY));
            add(new MessageStartWeek("8", "Pregnancy-week 8", ServiceType.PREGNANCY));
            add(new MessageStartWeek("9", "Pregnancy-week 9", ServiceType.PREGNANCY));
            add(new MessageStartWeek("10", "Pregnancy-week 10", ServiceType.PREGNANCY));
            add(new MessageStartWeek("11", "Pregnancy-week 11", ServiceType.PREGNANCY));
            add(new MessageStartWeek("12", "Pregnancy-week 12", ServiceType.PREGNANCY));
            add(new MessageStartWeek("13", "Pregnancy-week 13", ServiceType.PREGNANCY));
            add(new MessageStartWeek("14", "Pregnancy-week 14", ServiceType.PREGNANCY));
            add(new MessageStartWeek("15", "Pregnancy-week 15", ServiceType.PREGNANCY));
            add(new MessageStartWeek("16", "Pregnancy-week 16", ServiceType.PREGNANCY));
            add(new MessageStartWeek("17", "Pregnancy-week 17", ServiceType.PREGNANCY));
            add(new MessageStartWeek("18", "Pregnancy-week 18", ServiceType.PREGNANCY));
            add(new MessageStartWeek("19", "Pregnancy-week 19", ServiceType.PREGNANCY));
            add(new MessageStartWeek("20", "Pregnancy-week 20", ServiceType.PREGNANCY));
            add(new MessageStartWeek("21", "Pregnancy-week 21", ServiceType.PREGNANCY));
            add(new MessageStartWeek("22", "Pregnancy-week 22", ServiceType.PREGNANCY));
            add(new MessageStartWeek("23", "Pregnancy-week 23", ServiceType.PREGNANCY));
            add(new MessageStartWeek("24", "Pregnancy-week 24", ServiceType.PREGNANCY));
            add(new MessageStartWeek("25", "Pregnancy-week 25", ServiceType.PREGNANCY));
            add(new MessageStartWeek("26", "Pregnancy-week 26", ServiceType.PREGNANCY));
            add(new MessageStartWeek("27", "Pregnancy-week 27", ServiceType.PREGNANCY));
            add(new MessageStartWeek("28", "Pregnancy-week 28", ServiceType.PREGNANCY));
            add(new MessageStartWeek("29", "Pregnancy-week 29", ServiceType.PREGNANCY));
            add(new MessageStartWeek("30", "Pregnancy-week 30", ServiceType.PREGNANCY));
            add(new MessageStartWeek("31", "Pregnancy-week 31", ServiceType.PREGNANCY));
            add(new MessageStartWeek("32", "Pregnancy-week 32", ServiceType.PREGNANCY));
            add(new MessageStartWeek("33", "Pregnancy-week 33", ServiceType.PREGNANCY));
            add(new MessageStartWeek("34", "Pregnancy-week 34", ServiceType.PREGNANCY));
            add(new MessageStartWeek("35", "Pregnancy-week 35", ServiceType.PREGNANCY));
            add(new MessageStartWeek("36", "Pregnancy-week 36", ServiceType.PREGNANCY));
            add(new MessageStartWeek("37", "Pregnancy-week 37", ServiceType.PREGNANCY));
            add(new MessageStartWeek("38", "Pregnancy-week 38", ServiceType.PREGNANCY));
            add(new MessageStartWeek("39", "Pregnancy-week 39", ServiceType.PREGNANCY));
            add(new MessageStartWeek("40", "Pregnancy-week 40", ServiceType.PREGNANCY));
            add(new MessageStartWeek("41", "Baby-week 1", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("42", "Baby-week 2", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("43", "Baby-week 3", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("44", "Baby-week 4", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("45", "Baby-week 5", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("46", "Baby-week 6", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("47", "Baby-week 7", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("48", "Baby-week 8", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("49", "Baby-week 9", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("50", "Baby-week 10", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("51", "Baby-week 11", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("52", "Baby-week 12", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("53", "Baby-week 13", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("54", "Baby-week 14", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("55", "Baby-week 15", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("56", "Baby-week 16", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("57", "Baby-week 17", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("58", "Baby-week 18", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("59", "Baby-week 19", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("60", "Baby-week 20", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("61", "Baby-week 21", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("62", "Baby-week 22", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("63", "Baby-week 23", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("64", "Baby-week 24", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("65", "Baby-week 25", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("66", "Baby-week 26", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("67", "Baby-week 27", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("68", "Baby-week 28", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("69", "Baby-week 29", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("70", "Baby-week 30", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("71", "Baby-week 31", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("72", "Baby-week 32", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("73", "Baby-week 33", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("74", "Baby-week 34", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("75", "Baby-week 35", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("76", "Baby-week 36", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("77", "Baby-week 37", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("78", "Baby-week 38", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("79", "Baby-week 39", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("80", "Baby-week 40", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("81", "Baby-week 41", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("82", "Baby-week 42", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("83", "Baby-week 43", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("84", "Baby-week 44", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("85", "Baby-week 45", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("86", "Baby-week 46", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("87", "Baby-week 47", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("88", "Baby-week 48", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("89", "Baby-week 49", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("90", "Baby-week 50", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("91", "Baby-week 51", ServiceType.CHILD_CARE));
            add(new MessageStartWeek("92", "Baby-week 52", ServiceType.CHILD_CARE));
        }};
        assertThat(MessageStartWeek.messageStartWeeks(), is(equalTo(expectedMessageStartWeeks)));
    }
}
