package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.IVRCallCenterNoMapping;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.repository.AllIvrCallCenterNoMappings;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IvrCallCenterNoMappingServiceTest {

    @Mock
    private AllIvrCallCenterNoMappings allIvrCallCenterNoMappings;
    private IvrCallCenterNoMappingService ivrCallCenterNoMappingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ivrCallCenterNoMappingService = new IvrCallCenterNoMappingService(allIvrCallCenterNoMappings);
    }

    @Test
    public void shouldReturnTheCallCenterThatMatchedTheProvidedCriteria() {
        when(allIvrCallCenterNoMappings.allMappings()).thenReturn(Arrays.asList(
                new IVRCallCenterNoMapping().phoneNumber("phone_no1").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(10, 10)).endTime(new Time(10, 30)),
                new IVRCallCenterNoMapping().phoneNumber("phone_no2").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(10, 31)).endTime(new Time(11, 30)),
                new IVRCallCenterNoMapping().phoneNumber("phone_no3").language(Language.EN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(10, 10)).endTime(new Time(10, 30))
        ));

        assertThat(ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.EN, DayOfWeek.Monday, new Time(10, 10)), is(equalTo("phone_no1")));
        assertThat(ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.EN, DayOfWeek.Monday, new Time(10, 15)), is(equalTo("phone_no1")));
        assertThat(ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.EN, DayOfWeek.Monday, new Time(10, 30)), is(equalTo("phone_no1")));
        assertThat(ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.EN, DayOfWeek.Monday, new Time(10, 31)), is(equalTo("phone_no2")));
        assertThat(ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.EN, DayOfWeek.Monday, new Time(11, 30)), is(equalTo("phone_no2")));
        assertThat(ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.EN, DayOfWeek.Monday, new Time(9, 30)), is(nullValue()));
        assertThat(ivrCallCenterNoMappingService.getCallCenterPhoneNumber(Language.EN, DayOfWeek.Tuesday, new Time(10, 30)), is(equalTo("phone_no3")));
    }
}
