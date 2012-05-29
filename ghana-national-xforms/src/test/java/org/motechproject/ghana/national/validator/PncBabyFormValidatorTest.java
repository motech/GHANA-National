package org.motechproject.ghana.national.validator;

import org.junit.Test;
import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class PncBabyFormValidatorTest {

    @Test
    public void shouldValidatePNCBabyForm() {
        PncBabyFormValidator pncBabyFormValidator = spy(new PncBabyFormValidator());
        FormValidator mockFormValidator = mock(FormValidator.class);
        ReflectionTestUtils.setField(pncBabyFormValidator, "formValidator", mockFormValidator);

        PNCBabyForm pncBabyForm = new PNCBabyForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        pncBabyForm.setMotechId(motechId);
        pncBabyForm.setFacilityId(facilityId);
        pncBabyForm.setStaffId(staffId);

        final DependentValidator mockDependentValidator = mock(DependentValidator.class);
        when(pncBabyFormValidator.dependentValidator()).thenReturn(mockDependentValidator);

        final PatientValidator regClientFormValidations = new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForChild());
        PatientValidator expectedValidator = new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsAChild())).onFailure(regClientFormValidations);

        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(motechId)).thenReturn(patient);

        List<FormBean> formBeans = Arrays.<FormBean>asList(pncBabyForm);
        final FormBeanGroup formBeanGroup = new FormBeanGroup(formBeans);
        pncBabyFormValidator.validate(pncBabyForm, formBeanGroup, formBeans);

        verify(mockFormValidator).validateIfStaffExists(staffId);
        verify(mockFormValidator).validateIfFacilityExists(facilityId);
        verify(mockDependentValidator).validate(patient, formBeans, formBeans, expectedValidator);
    }
}
