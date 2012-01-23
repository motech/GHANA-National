package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.validator.MobileMidwifeValidator;
import org.motechproject.ghana.national.web.form.MobileMidwifeEnrollmentForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.TestUtils.isEq;
import static org.motechproject.ghana.national.web.MobileMidwifeController.MOBILE_MIDWIFE_URL;
import static org.motechproject.ghana.national.web.MobileMidwifeController.SUCCESS_MESSAGE;

public class MobileMidwifeControllerTest {

    @Mock
    private MobileMidwifeValidator mobileMidwifeValidator;
    @Mock
    private MobileMidwifeService mobileMidwifeService;
    @Mock
    private MessageSource messages;
    MobileMidwifeController controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new MobileMidwifeController(mobileMidwifeValidator, mobileMidwifeService, messages);
    }

    @Test
    public void shouldValidateAndCreateANewMobileMidwifeEnrollment() {
        String patientId = "patientId";
        String facilityId = "facilityI";
        String staffId = "staffId";
        MobileMidwifeEnrollmentForm form = defaultForm(patientId, facilityId, staffId, Medium.VOICE, new Time(23, 45));

        when(mobileMidwifeService.findBy(patientId)).thenReturn(null);
        when(mobileMidwifeValidator.validate(Matchers.<MobileMidwifeEnrollment>any())).thenReturn(Collections.<FormError>emptyList());

        ModelMap modelMap = new ModelMap();
        String editUrl = controller.save(form, null, modelMap);

        assertThat(editUrl, isEq(MOBILE_MIDWIFE_URL));
        ArgumentCaptor<MobileMidwifeEnrollment> enrollment = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);

        verify(mobileMidwifeValidator).validate(enrollment.capture());
        verify(mobileMidwifeService).register(enrollment.getValue());
        assertFormWithEnrollment((MobileMidwifeEnrollmentForm) modelMap.get("mobileMidwifeEnrollmentForm"), enrollment.getValue());
    }

    @Test
    public void shouldValidateAndUpdateExistingMobileMidwifeEnrollment() {
        String patientId = "patientId";
        String facilityId = "facilityI";
        String staffId = "staffId";
        String successMsg = "Changes successful";

        MobileMidwifeEnrollmentForm form = defaultForm(patientId, facilityId, staffId, Medium.SMS, new Time(23, 45));
        MobileMidwifeEnrollment existingEnrollment = defaultForm(patientId, "oldFacilityId", "oldStaffId", Medium.VOICE, new Time(23, 45)).createEnrollment();

        when(mobileMidwifeService.findBy(patientId)).thenReturn(existingEnrollment);
        when(mobileMidwifeValidator.validate(Matchers.<MobileMidwifeEnrollment>any())).thenReturn(Collections.<FormError>emptyList());
        when(messages.getMessage(SUCCESS_MESSAGE, null, Locale.getDefault())).thenReturn(successMsg);

        ModelMap modelMap = new ModelMap();
        String editUrl = controller.save(form, null, modelMap);

        ArgumentCaptor<MobileMidwifeEnrollment> enrollment = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mobileMidwifeValidator).validate(enrollment.capture());
        verify(mobileMidwifeService).register(enrollment.getValue());
        assertFormWithEnrollment((MobileMidwifeEnrollmentForm) modelMap.get("mobileMidwifeEnrollmentForm"), enrollment.getValue());

        assertThat(editUrl, isEq(MOBILE_MIDWIFE_URL));
        assertThat((String) modelMap.get("successMessage"), isEq(successMsg));
    }

    @Test
    public void shouldShowValidationErrors() {
        String patientId = "patientId";
        String facilityId = "facilityI";
        String staffId = "staffId";
        ModelMap map=new ModelMap();
        MobileMidwifeEnrollmentForm form = defaultForm(patientId, facilityId, staffId, Medium.VOICE, new Time(23, 45));

        when(mobileMidwifeValidator.validate(Matchers.<MobileMidwifeEnrollment>any())).thenReturn(new ArrayList<FormError>(){{
            add(new FormError("error1","description1"));
            add(new FormError("error2","description2"));
        }});

        String editUrl = controller.save(form, null, map);

        verify(mobileMidwifeValidator).validate(Matchers.<MobileMidwifeEnrollment>any());
        verify(mobileMidwifeService,never()).register((MobileMidwifeEnrollment)any());

        List<FormError> errors = (List<FormError>) map.get("formErrors");
        assertThat("description1",is(equalTo(errors.get(0).getError())));
        assertThat("description2",is(equalTo(errors.get(1).getError())));
        assertThat(editUrl, isEq(MOBILE_MIDWIFE_URL));
        assertEquals(form, map.get("mobileMidwifeEnrollmentForm"));
    }

    private MobileMidwifeEnrollmentForm defaultForm(String patientId, String facilityId, String staffId, Medium medium, Time timeOfDay) {
        return new MobileMidwifeEnrollmentForm()
                .setMedium(medium)
                .setPatientMotechId(patientId).setFacilityMotechId(facilityId).setStaffMotechId(staffId)
                .setTimeOfDay(timeOfDay).setConsent(true).setDayOfWeek(DayOfWeek.Monday).setLanguage(Language.EN)
                .setLearnedFrom(LearnedFrom.POSTERS_ADS).setMedium(Medium.SMS)
                .setMessageStartWeek("5").setPhoneNumber("9900011234")
                .setPhoneOwnership(PhoneOwnership.PERSONAL).setReasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH)
                .setServiceType(ServiceType.PREGNANCY);
    }

    private void assertFormWithEnrollment(MobileMidwifeEnrollmentForm form, MobileMidwifeEnrollment enrollment) {
        assertThat(form.getFacilityMotechId(), isEq(enrollment.getFacilityId()));
        assertThat(form.getPatientMotechId(), isEq(enrollment.getPatientId()));
        assertThat(form.getStaffMotechId(), isEq(enrollment.getStaffId()));
        assertThat(form.getReasonToJoin(), isEq(enrollment.getReasonToJoin()));
        assertThat(form.getConsent(), isEq(enrollment.getConsent()));
        assertThat(form.getDayOfWeek(), isEq(enrollment.getDayOfWeek()));
        assertThat(form.getLearnedFrom(), isEq(enrollment.getLearnedFrom()));
        assertThat(form.getPhoneNumber(), isEq(enrollment.getPhoneNumber()));
        assertThat(form.getPhoneOwnership(), isEq(enrollment.getPhoneOwnership()));
        assertThat(form.getTimeOfDay(), isEq(enrollment.getTimeOfDay()));
    }

}
