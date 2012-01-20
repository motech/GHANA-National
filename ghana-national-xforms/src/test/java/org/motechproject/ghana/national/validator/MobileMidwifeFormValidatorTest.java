package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.Time;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MobileMidwifeFormValidatorTest {
    private MobileMidwifeFormValidator mobileMidwifeFormValidator;

    @Mock
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Before
    public void setUp(){
        initMocks(this);
        mobileMidwifeFormValidator = new MobileMidwifeFormValidator();
        ReflectionTestUtils.setField(mobileMidwifeFormValidator, "formValidator", formValidator);
    }

    @Test
    public void shouldValidateMobileMidwifeForm() {
        MobileMidwifeForm formBean = mock(MobileMidwifeForm.class);
        String patientId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        when(formBean.getPatientId()).thenReturn(patientId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        mobileMidwifeFormValidator.validate(formBean);

        verify(formValidator).validatePatient( eq(patientId), eq(MobileMidwifeFormValidator.PATIENT_ID_ATTRIBUTE_NAME));
        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
    }

    @Test
    public void shouldValidateTimeOfDayForVoiceAsPreferredMedium() {
        MobileMidwifeForm form = with(new Time(22, 59)).buildMobileMidwifeForm();
        List<FormError> errors = mobileMidwifeFormValidator.validate(form);
        assertEquals(0, errors.size());

        form = with(new Time(5, 00)).buildMobileMidwifeForm();
        errors = mobileMidwifeFormValidator.validate(form);
        assertEquals(0, errors.size());

        form = with(new Time(4, 59)).buildMobileMidwifeForm();
        errors = mobileMidwifeFormValidator.validate(form);
        assertEquals(Constants.MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE, errors.get(0).getError());

        form = with(new Time(23, 01)).buildMobileMidwifeForm();
        errors = mobileMidwifeFormValidator.validate(form);
        assertEquals(Constants.MOBILE_MIDWIFE_VOICE_TIMEOFDAYRANGE_MESSAGE, errors.get(0).getError());
    }

    private MobileMidwifeBuilder with(Time timeOfDay) {
        return new MobileMidwifeBuilder().patientId("1234568").staffId("465").facilityId("13161").consent(true)
                .format("PERS_VOICE").timeOfDay(timeOfDay);
    }


}
