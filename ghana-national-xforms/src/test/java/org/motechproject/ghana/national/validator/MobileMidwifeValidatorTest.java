package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.Time;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

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
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment().setPatientId(patientId).setFacilityId(facilityId)
                .setStaffId(staffId);

        mobileMidwifeValidator.validate(enrollment);

        verify(formValidator).validatePatient(eq(patientId), eq(MobileMidwifeValidator.PATIENT_ID_ATTRIBUTE_NAME));
        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
    }

    @Test
    public void shouldValidatePreferredTimeOfDayExistForVoiceAndNonPublicPhoneOwners() {
        MobileMidwifeEnrollment enrollment = with(new Time(22, 59)).setPhoneOwnership(PhoneOwnership.PERSONAL);
        List<FormError> errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(0, errors.size());

        enrollment = with(null).setPhoneOwnership(PhoneOwnership.PERSONAL);
        errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(Constants.MOBILE_MIDWIFE_VOICE_TIMEOFDAYREQUIRED_MESSAGE, errors.get(0).getError());

        enrollment = with(null).setPhoneOwnership(PhoneOwnership.PUBLIC);
        errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(0, errors.size());

        enrollment = with(new Time(null, null)).setPhoneOwnership(PhoneOwnership.PUBLIC);
        errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(0, errors.size());
    }

    @Test
    public void shouldValidatePreferredTimeOfDayWithinRangeForVoiceAsPreferredMedium() {
        MobileMidwifeEnrollment enrollment = with(new Time(22, 59));
        List<FormError> errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(0, errors.size());

        enrollment = with(new Time(5, 0));
        errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(0, errors.size());

        enrollment = with(new Time(4, 59));
        errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(Constants.MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE, errors.get(0).getError());

        enrollment = with(new Time(23, 1));
        errors = mobileMidwifeValidator.validate(enrollment);
        assertEquals(Constants.MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE, errors.get(0).getError());
    }

    @Test
    public void shouldValidateFieldValuesForEnrollment() {

        MobileMidwifeEnrollment enrollment = with(new Time(5,1));
        mobileMidwifeValidator = spy(mobileMidwifeValidator);
        mobileMidwifeValidator.validateFieldValues(enrollment);
        
        verify(mobileMidwifeValidator).validateTime(enrollment);
        verify(formValidator, never()).validatePatient(anyString(), anyString());
        verify(formValidator, never()).validateIfStaffExists(anyString());
        verify(formValidator, never()).validateIfFacilityExists(anyString());
    }

    @Test
    public void shouldValidateIncludeFormCheckForCommonFieldValuesAnd_NeverCheckForFacilityPatientAndStaffExistence() {

        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment().setPatientId("1234567").setFacilityId("13161")
                .setStaffId("465");
        mobileMidwifeValidator = spy(mobileMidwifeValidator);
        doReturn(emptyList()).when(mobileMidwifeValidator).validateFieldValues(enrollment);

        mobileMidwifeValidator.validateForIncludeForm(enrollment);

        verify(mobileMidwifeValidator).validateFieldValues(enrollment);
    }

    private MobileMidwifeEnrollment with(Time timeOfDay) {
        return  new MobileMidwifeEnrollment().setPatientId("1234568").setFacilityId("465")
                .setStaffId("13161").setConsent(true).setMedium(Medium.VOICE).setTimeOfDay(timeOfDay);
    }


}
