package org.motechproject.ghana.national.validator;


import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutPatientVisitFormValidatorTest {

    OutPatientVisitFormValidator validator=new OutPatientVisitFormValidator();
    @Mock
    FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(validator, "formValidator", formValidator);
    }

    @Test
    public void shouldVerifyValidationCallsIfPatientIsAFemale() {
        OutPatientVisitForm formBean = new OutPatientVisitForm();
        formBean.setRegistrantType(PatientType.PREGNANT_MOTHER);
        setExpectations(formBean);

        validator.validate(formBean);

        verifyValidations(formBean);
        verify(formValidator).validateIfPatientIsFemale(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME);

    }

    @Test
    public void shouldVerifyValidationCallsIfPatientIsAChild() {
        OutPatientVisitForm formBean = new OutPatientVisitForm();
        formBean.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        setExpectations(formBean);

        validator.validate(formBean);

        verifyValidations(formBean);
        verify(formValidator).validateIfPatientIsAChild(formBean.getMotechId());
    }

    private void setExpectations(OutPatientVisitForm formBean) {
        when(formValidator.validateIfStaffExists(formBean.getStaffId())).thenReturn(new ArrayList<FormError>());
        when(formValidator.validateIfFacilityExists(formBean.getFacilityId())).thenReturn(new ArrayList<FormError>());
        when(formValidator.validatePatient(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME)).thenReturn(new ArrayList<FormError>());
    }

    private void verifyValidations(OutPatientVisitForm formBean) {

        verify(formValidator).validateIfStaffExists(formBean.getStaffId());
        verify(formValidator).validateIfFacilityExists(formBean.getFacilityId());
        verify(formValidator).validatePatient(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME);
    }

    @Test
    public void shouldReturnFormErrors() {
        final OutPatientVisitForm outPatientVisitForm = new OutPatientVisitForm();
        outPatientVisitForm.setMotechId("111");
        outPatientVisitForm.setStaffId("222");
        outPatientVisitForm.setFacilityId("33");
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
        when(formValidator.validateIfStaffExists(outPatientVisitForm.getStaffId())).thenReturn(staffFormErrors);
        when(formValidator.validateIfFacilityExists(outPatientVisitForm.getFacilityId())).thenReturn(facilityFormErrors);
        when(formValidator.validatePatient(outPatientVisitForm.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME)).thenReturn(patientFormErrors);

        final List<FormError> actualFormErrors = validator.validate(outPatientVisitForm);

        assertTrue(CollectionUtils.isSubCollection(staffFormErrors, actualFormErrors));
        assertTrue(CollectionUtils.isSubCollection(facilityFormErrors, actualFormErrors));
        assertTrue(CollectionUtils.isSubCollection(patientFormErrors, actualFormErrors));
    }

}
