package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.IVRCallCenterNoMapping;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AllIvrCallCenterNoMappingsTest extends BaseIntegrationTest {

    @Autowired
    AllIvrCallCenterNoMappings allIvrCallCenterNoMappings;

    @Test
    public void shouldBeAbleToFindMappingBasedOnLanguageDayOfWeekAndTime(){
        allIvrCallCenterNoMappings.add(new IVRCallCenterNoMapping().language(Language.EN).dayOfWeek(DayOfWeek.Monday)
                .startTime(new Time(10, 10)).endTime(new Time(14, 10)).phoneNumber("phone_no1"));
        allIvrCallCenterNoMappings.add(new IVRCallCenterNoMapping().language(Language.KAS).dayOfWeek(DayOfWeek.Monday)
                .startTime(new Time(10, 10)).endTime(new Time(14, 10)).phoneNumber("phone_no2"));
        allIvrCallCenterNoMappings.add(new IVRCallCenterNoMapping().language(Language.EN).dayOfWeek(DayOfWeek.Tuesday)
                .startTime(new Time(10, 10)).endTime(new Time(14, 10)).phoneNumber("phone_no3"));
        allIvrCallCenterNoMappings.add(new IVRCallCenterNoMapping().language(Language.EN).dayOfWeek(DayOfWeek.Monday)
                .startTime(new Time(15, 10)).endTime(new Time(17, 10)).phoneNumber("phone_no4"));
        allIvrCallCenterNoMappings.add(new IVRCallCenterNoMapping().language(Language.EN).dayOfWeek(DayOfWeek.Saturday)
                .startTime(new Time(10, 10)).endTime(new Time(14, 10)).phoneNumber("phone_no5"));

        assertThat(allIvrCallCenterNoMappings.findByLangDayAndTime(Language.EN, DayOfWeek.Monday, new Time(10, 10), new Time(14, 10)).getPhoneNumber(),
                is(equalTo("phone_no1")));
        assertThat(allIvrCallCenterNoMappings.findByLangDayAndTime(Language.KAS, DayOfWeek.Monday, new Time(10, 10), new Time(14, 10)).getPhoneNumber(),
                is(equalTo("phone_no2")));
        assertThat(allIvrCallCenterNoMappings.findByLangDayAndTime(Language.EN, DayOfWeek.Tuesday, new Time(10, 10), new Time(14, 10)).getPhoneNumber(),
                is(equalTo("phone_no3")));
        assertThat(allIvrCallCenterNoMappings.findByLangDayAndTime(Language.EN, DayOfWeek.Monday, new Time(15, 10), new Time(17, 10)).getPhoneNumber(),
                is(equalTo("phone_no4")));
        assertThat(allIvrCallCenterNoMappings.findByLangDayAndTime(Language.EN, DayOfWeek.Saturday, new Time(10, 10), new Time(14, 10)).getPhoneNumber(),
                is(equalTo("phone_no5")));
    }

    @After
    public void tearDown() {
        List<IVRCallCenterNoMapping> all = allIvrCallCenterNoMappings.getAll();
        for (IVRCallCenterNoMapping mapping : all)
            allIvrCallCenterNoMappings.remove(mapping);
    }
}
