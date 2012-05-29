package org.motechproject.ghana.national.validator;

import org.junit.Test;
import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class PncMotherFormValidatorTest {

    @Test
    public void shouldValidatePNCMotherForm() {
        PncMotherFormValidator pncMotherFormValidator = spy(new PncMotherFormValidator());
        FormValidator mockFormValidator = mock(FormValidator.class);
        ReflectionTestUtils.setField(pncMotherFormValidator, "formValidator", mockFormValidator);

        PNCMotherForm pncMotherForm = new PNCMotherForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        pncMotherForm.setMotechId(motechId);
        pncMotherForm.setFacilityId(facilityId);
        pncMotherForm.setStaffId(staffId);

        final DependentValidator mockDependentValidator = mock(DependentValidator.class);
        when(pncMotherFormValidator.dependentValidator()).thenReturn(mockDependentValidator);

        final PatientValidator regClientFormValidations = new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForFemale());
        PatientValidator expectedValidator = new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsFemale())).onFailure(regClientFormValidations);

        Patient patient = mock(Patient.class);
        when(mockFormValidator.getPatient(motechId)).thenReturn(patient);

        List<FormBean> formBeans = Arrays.<FormBean>asList(pncMotherForm);
        final FormBeanGroup formBeanGroup = new FormBeanGroup(formBeans);
        pncMotherFormValidator.validate(pncMotherForm, formBeanGroup, formBeans);

        verify(mockFormValidator).validateIfStaffExists(staffId);
        verify(mockFormValidator).validateIfFacilityExists(facilityId);
        verify(mockDependentValidator).validate(patient, formBeans, formBeans, expectedValidator);
    }
}
