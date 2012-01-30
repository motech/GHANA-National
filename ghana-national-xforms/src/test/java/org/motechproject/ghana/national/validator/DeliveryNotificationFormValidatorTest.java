package org.motechproject.ghana.national.validator;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeliveryNotificationFormValidatorTest {

    DeliveryNotificationFormValidator validator = new DeliveryNotificationFormValidator();

    @Mock
    FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(validator, "formValidator", formValidator);
    }

    @Test
    public void shouldVerifyValidationCalls() {
        DeliveryNotificationForm formBean = new DeliveryNotificationForm();
        validator.validate(formBean);
        verify(formValidator).validateIfStaffExists(formBean.getStaffId());
        verify(formValidator).validateIfFacilityExists(formBean.getFacilityId());
        verify(formValidator).validatePatient(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME);
    }

    @Test
    public void shouldReturnFormErrors() {
        final DeliveryNotificationForm deliveryNotificationForm = new DeliveryNotificationForm();
        deliveryNotificationForm.setMotechId("111");
        deliveryNotificationForm.setStaffId("222");
        deliveryNotificationForm.setFacilityId("33");
        List<FormError> staffFormErrors = new ArrayList<FormError>() {
            {
                add(new FormError("test1", "error1"));
            }
        };
        List<FormError> facilityFormErrors = new ArrayList<FormError>() {
            {
                add(new FormError("test2", "error2"));
            }
        };
        List<FormError> patientFormErrors = new ArrayList<FormError>() {
            {
                add(new FormError("test3", "error3"));
            }
        };
        when(formValidator.validateIfStaffExists(deliveryNotificationForm.getStaffId())).thenReturn(staffFormErrors);
        when(formValidator.validateIfFacilityExists(deliveryNotificationForm.getFacilityId())).thenReturn(facilityFormErrors);
        when(formValidator.validatePatient(deliveryNotificationForm.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME)).thenReturn(patientFormErrors);

        final List<FormError> actualFormErrors = validator.validate(deliveryNotificationForm);

        assertTrue(CollectionUtils.isSubCollection(staffFormErrors, actualFormErrors));
        assertTrue(CollectionUtils.isSubCollection(facilityFormErrors, actualFormErrors));
        assertTrue(CollectionUtils.isSubCollection(patientFormErrors, actualFormErrors));
    }
}
