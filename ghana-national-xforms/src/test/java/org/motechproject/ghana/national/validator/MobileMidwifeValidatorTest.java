package org.motechproject.ghana.national.validator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.*;

public class MobileMidwifeValidatorTest {

    private MobileMidwifeValidator mobileMidwifeValidator;

    @Mock
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        mobileMidwifeValidator = new MobileMidwifeValidator();
        ReflectionTestUtils.setField(mobileMidwifeValidator, "formValidator", formValidator);
    }

    @Test
    public void shouldCheckIfPatientStaffAndFacilityExistsForMobileMidwifeEnrollment() {
        String patientId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(DateTime.now()).setPatientId(patientId).setFacilityId(facilityId)
                .setStaffId(staffId);

        Patient patient = new Patient(new MRSPatient(patientId,new MRSPerson().dead(false),new MRSFacility(facilityId)));
        when(formValidator.getPatient(patientId)).thenReturn(patient);
        List<FormError> formErrors = mobileMidwifeValidator.validate(enrollment);

        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
        assertThat(formErrors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND))));
        assertThat(formErrors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE))));

    }

    @Test
    public void shouldValidatePreferredTimeOfDayExistForVoiceAndNonPublicPhoneOwners() {
        MobileMidwifeEnrollment enrollment = with(new Time(22, 59)).setPhoneOwnership(PhoneOwnership.PERSONAL).setPatientId("motechId");
        Patient patient = new Patient(new MRSPatient("motechId",new MRSPerson().dead(false),new MRSFacility("facilityId")));
        when(formValidator.getPatient("motechId")).thenReturn(patient);
        List<FormError> errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(0, errors.size());

        enrollment = with(null).setPhoneOwnership(PhoneOwnership.PERSONAL);
        errors = mobileMidwifeValidator.validate(enrollment);
        assertThat(errors, hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYREQUIRED_MESSAGE)));

        enrollment = with(null).setPhoneOwnership(PhoneOwnership.PUBLIC);
        when(formValidator.getPatient("motechId")).thenReturn(patient);
        errors = mobileMidwifeValidator.validate(enrollment);
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE))));
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYREQUIRED_MESSAGE))));

        enrollment = with(new Time(null, null)).setPhoneOwnership(PhoneOwnership.PUBLIC);
        when(formValidator.getPatient("motechId")).thenReturn(patient);
        errors = mobileMidwifeValidator.validate(enrollment);
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE))));
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYREQUIRED_MESSAGE))));
    }

    @Test
    public void shouldValidatePreferredTimeOfDayWithinRangeForVoiceAsPreferredMedium() {
        MobileMidwifeEnrollment enrollment = with(new Time(22, 59));
        List<FormError> errors = mobileMidwifeValidator.validate(enrollment);
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE))));
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYREQUIRED_MESSAGE))));


        enrollment = with(new Time(5, 0));
        errors = mobileMidwifeValidator.validate(enrollment);
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE))));
        assertThat(errors, not(hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYREQUIRED_MESSAGE))));


        enrollment = with(new Time(4, 59));
        errors = mobileMidwifeValidator.validate(enrollment);
        assertThat(errors, hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE)));

        enrollment = with(new Time(23, 1));
        errors = mobileMidwifeValidator.validate(enrollment);
        assertThat(errors, hasItem(new FormError("", MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE)));
    }

    @Test
    public void shouldValidateFieldValuesForEnrollment() {

        MobileMidwifeEnrollment enrollment = with(new Time(5,1));
        mobileMidwifeValidator = spy(mobileMidwifeValidator);
        mobileMidwifeValidator.validateFieldValues(enrollment);
        
        verify(mobileMidwifeValidator).validateTime(enrollment);
        verify(formValidator, never()).validateIfStaffExists(anyString());
        verify(formValidator, never()).validateIfFacilityExists(anyString());
    }

    @Test
    public void shouldValidateIncludeFormCheckForCommonFieldValuesAnd_NeverCheckForFacilityPatientAndStaffExistence() {

        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(DateTime.now()).setPatientId("1234567").setFacilityId("13161")
                .setStaffId("465");
        mobileMidwifeValidator = spy(mobileMidwifeValidator);
        doReturn(emptyList()).when(mobileMidwifeValidator).validateFieldValues(enrollment);

        mobileMidwifeValidator.validateForIncludeForm(enrollment);

        verify(mobileMidwifeValidator).validateFieldValues(enrollment);
    }

    private MobileMidwifeEnrollment with(Time timeOfDay) {
        return  new MobileMidwifeEnrollment(DateTime.now()).setPatientId("1234568").setFacilityId("465")
                .setStaffId("13161").setConsent(true).setMedium(Medium.VOICE).setTimeOfDay(timeOfDay);
    }


}
