package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.MobileMidwifeForm;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
}
