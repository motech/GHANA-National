package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.DeliveryForm;
import org.motechproject.ghana.national.domain.Constants;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeliveryFormValidatorTest {
    
    @Mock
    FormValidator mockFormValidator;

    DeliveryFormValidator deliveryFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        deliveryFormValidator = new DeliveryFormValidator();
        ReflectionTestUtils.setField(deliveryFormValidator, "formValidator", mockFormValidator);
    }

    @Test
    public void shouldValidateDeliveryForm() {
        DeliveryForm formBean = new DeliveryForm();
        String facilityId = "13131";
        String staffId = "465";
        String motechId = "12543";

        formBean.setFacilityId(facilityId);
        formBean.setStaffId(staffId);
        formBean.setMotechId(motechId);
        deliveryFormValidator.validate(formBean);
        verify(mockFormValidator).validateIfStaffExists(staffId);
        verify(mockFormValidator).validateIfFacilityExists(facilityId);
        verify(mockFormValidator).validatePatient(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        verify(mockFormValidator).validateIfPatientIsFemale(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        
    }
}
