package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class MobileMidwifeFormHandlerTest {

    @Mock
    private MobileMidwifeService mockMobileMidwifeService;

    MobileMidwifeFormHandler formHandler;

    @Before
    public void setUp() {
         initMocks(this);
        formHandler = new MobileMidwifeFormHandler();
        ReflectionTestUtils.setField(formHandler, "mobileMidwifeService", mockMobileMidwifeService);
    }

    @Test
    public void shouldSaveMobileMidwifeEnrollmentForm() {
        final String staffId = "11";
        final String facilityId = "11";
        final MobileMidwifeForm form = new MobileMidwifeBuilder().staffId(staffId).facilityId(facilityId).patientId("patientId")
                .consent(true).dayOfWeek(DayOfWeek.Monday).language(Language.EN).learnedFrom(LearnedFrom.FRIEND).format("PERS_VOICE")
                .timeOfDay(new Time(10, 0)).messageStartWeek("10").phoneNumber("9500012343")
                .phoneOwnership(PhoneOwnership.PERSONAL).reasonToJoin(ReasonToJoin.FAMILY_FRIEND_DELIVERED)
                .serviceType(ServiceType.CHILD_CARE)
                .buildMobileMidwifeForm();

        formHandler.handleFormEvent(new MotechEvent("", new HashMap<String, Object>(){{
            put("formBean", form);
        }}));

        final ArgumentCaptor<MobileMidwifeEnrollment> captor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mockMobileMidwifeService).register(captor.capture());
        final MobileMidwifeEnrollment enrollment = captor.getValue();

        assertFormWithEnrollment(form, enrollment);
    }

    private void assertFormWithEnrollment(MobileMidwifeForm exptectedForm, MobileMidwifeEnrollment actual) {
        assertThat(actual.getStaffId(), is(exptectedForm.getStaffId()));
        assertThat(actual.getFacilityId(), is(exptectedForm.getFacilityId()));
        assertThat(actual.getPatientId(), is(exptectedForm.getPatientId()));
        assertThat(actual.getServiceType(), is(exptectedForm.getServiceType()));
        assertThat(actual.getReasonToJoin(), is(exptectedForm.getReasonToJoin()));
        assertThat(actual.getMedium(), is(exptectedForm.getMediumStripingOwnership()));
        assertThat(actual.getDayOfWeek(), is(exptectedForm.getDayOfWeek()));
        assertThat(actual.getTimeOfDay(), is(exptectedForm.getTimeOfDay()));
        assertThat(actual.getLanguage(), is(exptectedForm.getLanguage()));
        assertThat(actual.getLearnedFrom(), is(exptectedForm.getLearnedFrom()));
        assertThat(actual.getPhoneNumber(), is(exptectedForm.getPhoneNumber()));
        assertThat(actual.getPhoneOwnership(), is(exptectedForm.getPhoneOwnership()));
        assertThat(actual.getConsent(), is(exptectedForm.getConsent()));
    }
}
