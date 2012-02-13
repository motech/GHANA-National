package org.motechproject.ghana.national.validator;


import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
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
        formBean.setVisitor(false);
        setExpectations(formBean);

        validator.validate(formBean);

        verifyValidations(formBean);
        verify(formValidator).validateIfPatientIsFemale(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME);

    }

    @Test
    public void shouldVerifyValidationCallsIfPatientIsAChild() {
        OutPatientVisitForm formBean = new OutPatientVisitForm();
        formBean.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        formBean.setVisitor(false);
        setExpectations(formBean);

        validator.validate(formBean);

        verifyValidations(formBean);
        verify(formValidator).validateIfPatientIsAChild(formBean.getMotechId());
    }

    private void setExpectations(OutPatientVisitForm formBean) {
        when(formValidator.validateIfStaffExists(formBean.getStaffId())).thenReturn(new ArrayList<FormError>());
        when(formValidator.validateIfFacilityExists(formBean.getFacilityId())).thenReturn(new ArrayList<FormError>());
        when(formValidator.validateIfPatientExistsAndIsAlive(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME)).thenReturn(new ArrayList<FormError>());
    }

    private void verifyValidations(OutPatientVisitForm formBean) {

        verify(formValidator).validateIfStaffExists(formBean.getStaffId());
        verify(formValidator).validateIfFacilityExists(formBean.getFacilityId());
        verify(formValidator).validateIfPatientExistsAndIsAlive(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME);
    }

    @Test
    public void shouldReturnFormErrors() {
        final OutPatientVisitForm outPatientVisitForm = new OutPatientVisitForm();
        outPatientVisitForm.setMotechId("111");
        outPatientVisitForm.setStaffId("222");
        outPatientVisitForm.setFacilityId("33");
        outPatientVisitForm.setVisitor(false);
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
        when(formValidator.validateIfPatientExistsAndIsAlive(outPatientVisitForm.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME)).thenReturn(patientFormErrors);

        final List<FormError> actualFormErrors = validator.validate(outPatientVisitForm);

        assertTrue(CollectionUtils.isSubCollection(staffFormErrors, actualFormErrors));
        assertTrue(CollectionUtils.isSubCollection(facilityFormErrors, actualFormErrors));
        assertTrue(CollectionUtils.isSubCollection(patientFormErrors, actualFormErrors));
    }

    @Test
    public void shouldNotValidatePatientIfVisitor(){
        OutPatientVisitForm formBean = new OutPatientVisitForm();
        formBean.setVisitor(true);

        validator.validate(formBean);

        verify(formValidator).validateIfStaffExists(formBean.getStaffId());
        verify(formValidator).validateIfFacilityExists(formBean.getFacilityId());
        verifyNoMoreInteractions(formValidator);
    }

}
