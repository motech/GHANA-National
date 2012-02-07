package org.motechproject.ghana.national.web;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.validator.MobileMidwifeValidator;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.form.MobileMidwifeEnrollmentForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;

import java.util.*;

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

    MobileMidwifeController controller;

    @Mock
    private MobileMidwifeValidator mobileMidwifeValidator;
    @Mock
    private MobileMidwifeService mobileMidwifeService;
    @Mock
    private MessageSource messages;
    @Mock
    private FacilityHelper mockFacilityHelper;
    @Mock
    private FacilityService mockFacilityService;
    private static final String ACTION_REGISTER = "REGISTER";
    private static final String ACTION_UNREGISTER = "UN_REGISTER";

    @Before
    public void setUp() {
        initMocks(this);
        controller = new MobileMidwifeController(mobileMidwifeValidator, mobileMidwifeService, messages, mockFacilityHelper, mockFacilityService);
    }

    @Test
    public void shouldRetrieveEnrollmentForAPatient() {
        String patientId = "12344";
        MobileMidwifeEnrollment enrollment = MobileMidwifeEnrollment.newEnrollment();
        enrollment.setActive(true);
        String locationId = "facilityMotechId";
        enrollment.setFacilityId(locationId);
        when(mobileMidwifeService.findLatestEnrollment(patientId)).thenReturn(enrollment);
        when(mockFacilityHelper.locationMap()).thenReturn(new HashMap());
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacilityByMotechId(locationId)).thenReturn(mockFacility);
        String facilityId = "13161";
        when(mockFacility.getMotechId()).thenReturn(facilityId);

        ModelMap modelMap = new ModelMap();
        controller.form(patientId, modelMap);

        MobileMidwifeEnrollmentForm enrollmentForm = (MobileMidwifeEnrollmentForm) modelMap.get("mobileMidwifeEnrollmentForm");
        assertFormWithEnrollment(enrollmentForm, enrollment);
        assertThat(enrollmentForm.getStatus(), is("ACTIVE"));

    }

    @Test
    public void shouldCreateMobileMidwifeEnrollmentFromEnrollmentForm() {
        String locationId = "54";
        MobileMidwifeEnrollmentForm enrollmentForm = createEnrollmentForm("12344", locationId, "345", Medium.SMS, new Time(23, 45), ACTION_REGISTER);
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(locationId)).thenReturn(mockFacility);
        String facilityId = "13161";
        when(mockFacility.getMotechId()).thenReturn(facilityId);

        MobileMidwifeEnrollment midwifeEnrollment = controller.createEnrollment(enrollmentForm);
        assertEquals(facilityId, midwifeEnrollment.getFacilityId());
        assertFormWithEnrollment(enrollmentForm, midwifeEnrollment);
        assertEquals(new LocalDate(), midwifeEnrollment.getEnrollmentDateTime().toLocalDate());
    }

    @Test
    public void shouldValidateAndCreateANewMobileMidwifeEnrollmentIfActionIsRegister() {
        String patientId = "patientId";
        String locationId = "54";
        MobileMidwifeEnrollmentForm form = createEnrollmentForm("12344", locationId, "345", Medium.SMS, new Time(23, 45), ACTION_REGISTER);

        when(mobileMidwifeService.findActiveBy(patientId)).thenReturn(null);
        when(mobileMidwifeValidator.validate(Matchers.<MobileMidwifeEnrollment>any())).thenReturn(Collections.<FormError>emptyList());
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(locationId)).thenReturn(mockFacility);
        when(mockFacility.getMotechId()).thenReturn("13161");

        ModelMap modelMap = new ModelMap();
        String editUrl = controller.save(form, null, modelMap);

        assertThat(editUrl, isEq(MOBILE_MIDWIFE_URL));
        ArgumentCaptor<MobileMidwifeEnrollment> enrollment = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);

        verify(mobileMidwifeValidator).validate(enrollment.capture());
        verify(mobileMidwifeService).register(enrollment.getValue());
        assertFormWithEnrollment((MobileMidwifeEnrollmentForm) modelMap.get("mobileMidwifeEnrollmentForm"), enrollment.getValue());
    }

    @Test
    public void shouldInActivateMobileMidwifeEnrollmentIfActionIsUnRegister() {
        String locationId = "54";
        String patientId = "12344";
        MobileMidwifeEnrollmentForm form = createEnrollmentForm(patientId, locationId, "345", Medium.SMS, new Time(23, 45), ACTION_UNREGISTER);

        MobileMidwifeEnrollment mockEnrollment = mock(MobileMidwifeEnrollment.class);
        when(mobileMidwifeService.findActiveBy(patientId)).thenReturn(mockEnrollment);
        when(mobileMidwifeValidator.validate(Matchers.<MobileMidwifeEnrollment>any())).thenReturn(Collections.<FormError>emptyList());
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(locationId)).thenReturn(mockFacility);
        when(mockFacility.getMotechId()).thenReturn("13161");

        ModelMap modelMap = new ModelMap();
        String editUrl = controller.save(form, null, modelMap);

        assertThat(editUrl, isEq(MOBILE_MIDWIFE_URL));
        ArgumentCaptor<MobileMidwifeEnrollment> enrollment = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);

        verify(mobileMidwifeValidator).validate(enrollment.capture());
        verify(mobileMidwifeService).unregister(patientId);
        assertFormWithEnrollment((MobileMidwifeEnrollmentForm) modelMap.get("mobileMidwifeEnrollmentForm"), enrollment.getValue());
    }

    @Test
    public void shouldValidateAndUpdateExistingMobileMidwifeEnrollment() {
        String patientId = "patientId";
        String locationId = "54";
        String successMsg = "Changes successful";

        MobileMidwifeEnrollmentForm form = createEnrollmentForm(patientId, locationId, "staffId", Medium.SMS, new Time(23, 45), ACTION_REGISTER);
        MobileMidwifeEnrollment existingEnrollment = MobileMidwifeEnrollment.newEnrollment();
        existingEnrollment.setPatientId(patientId);
        existingEnrollment.setFacilityId("oldFacilityId");
        existingEnrollment.setStaffId("oldStaffId");
        existingEnrollment.setMedium(Medium.VOICE);
        existingEnrollment.setTimeOfDay(new Time(23, 45));

        when(mobileMidwifeService.findActiveBy(patientId)).thenReturn(existingEnrollment);
        when(mobileMidwifeValidator.validate(Matchers.<MobileMidwifeEnrollment>any())).thenReturn(Collections.<FormError>emptyList());
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(locationId)).thenReturn(mockFacility);
        when(mockFacility.getMotechId()).thenReturn("13161");
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
        ModelMap map = new ModelMap();
        String locationId = "54";

        MobileMidwifeEnrollmentForm enrollmentForm = createEnrollmentForm("patientId", locationId, "staffId", Medium.VOICE, new Time(23, 45), ACTION_REGISTER);
        when(mobileMidwifeValidator.validate(Matchers.<MobileMidwifeEnrollment>any())).thenReturn(new ArrayList<FormError>() {{
            add(new FormError("error1", "description1"));
            add(new FormError("error2", "description2"));
        }});
        Facility mockFacility = mock(Facility.class);
        when(mockFacilityService.getFacility(locationId)).thenReturn(mockFacility);
        when(mockFacility.getMotechId()).thenReturn("13161");

        String editUrl = controller.save(enrollmentForm, null, map);

        verify(mobileMidwifeValidator).validate(Matchers.<MobileMidwifeEnrollment>any());
        verify(mobileMidwifeService, never()).register((MobileMidwifeEnrollment) any());

        List<FormError> errors = (List<FormError>) map.get("formErrors");
        assertThat("description1", is(equalTo(errors.get(0).getError())));
        assertThat("description2", is(equalTo(errors.get(1).getError())));
        assertThat(editUrl, isEq(MOBILE_MIDWIFE_URL));
        assertEquals(enrollmentForm, map.get("mobileMidwifeEnrollmentForm"));
    }


    private MobileMidwifeEnrollmentForm createEnrollmentForm(String patientId, String locationId, String staffId, Medium medium, Time timeOfDay, String action) {
        FacilityForm facilityForm = new FacilityForm();
        facilityForm.setFacilityId(locationId);
        MobileMidwifeEnrollmentForm form = new MobileMidwifeEnrollmentForm()
                .setMedium(medium)
                .setPatientMotechId(patientId).setFacilityForm(facilityForm).setStaffMotechId(staffId)
                .setTimeOfDay(timeOfDay).setConsent(true).setDayOfWeek(DayOfWeek.Monday).setLanguage(Language.EN)
                .setLearnedFrom(LearnedFrom.POSTERS_ADS).setMedium(Medium.SMS)
                .setMessageStartWeek("5").setPhoneNumber("9900011234")
                .setPhoneOwnership(PhoneOwnership.PERSONAL).setReasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH)
                .setServiceType(ServiceType.PREGNANCY).setFacilityForm(facilityForm).setAction(action);

        return form;
    }


    private void assertFormWithEnrollment(MobileMidwifeEnrollmentForm form, MobileMidwifeEnrollment enrollment) {
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
