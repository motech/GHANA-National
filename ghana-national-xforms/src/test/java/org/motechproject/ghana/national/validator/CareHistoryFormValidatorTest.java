package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.ExistsInDb;
import org.motechproject.ghana.national.validator.patient.IsAlive;
import org.motechproject.ghana.national.validator.patient.PatientValidator;
import org.motechproject.ghana.national.validator.patient.RegClientFormSubmittedInSameUpload;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CareHistoryFormValidatorTest {
    private CareHistoryFormValidator careHistoryFormValidator;
    @Mock
    private FormValidator mockFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        careHistoryFormValidator = spy(new CareHistoryFormValidator());
        ReflectionTestUtils.setField(careHistoryFormValidator, "formValidator", mockFormValidator);
    }

    @Test
    public void shouldValidateIfCareHistoryFormBeanIsValid() {
        String staffId = "1234567";
        String facilityId = "098765";
        String motechId = "0234567";

        CareHistoryForm formBean = careHistoryFormBean(staffId, facilityId, motechId);
        Patient patient = new Patient();
        when(mockFormValidator.getPatient(motechId)).thenReturn(patient);
        PatientValidator expectedValidators = new ExistsInDb().onSuccess(new IsAlive()).onFailure(new RegClientFormSubmittedInSameUpload());
        DependentValidator dependentValidator = mock(DependentValidator.class);
        when(careHistoryFormValidator.dependentValidator()).thenReturn(dependentValidator);
        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean);
        FormBeanGroup group = new FormBeanGroup(formBeans);
        List<FormError> formErrors = careHistoryFormValidator.validate(formBean, group, formBeans);
        assertFalse(formErrors.isEmpty());
        assertFalse(select(formErrors, having(on(FormError.class).getParameter(), is("staffId"))).isEmpty());
        assertFalse(select(formErrors, having(on(FormError.class).getParameter(), is("facilityId"))).isEmpty());
        assertFalse(select(formErrors, having(on(FormError.class).getParameter(), is("motechId"))).isEmpty());

        verify(mockFormValidator).validateIfFacilityExists(facilityId);
        verify(mockFormValidator).validateIfStaffExists(staffId);
        verify(dependentValidator).validate(patient,group.getFormBeans(), formBeans, expectedValidators);
    }

    private CareHistoryForm careHistoryFormBean(String staffId, String facilityId, String motechId) {
        CareHistoryForm formBean = new CareHistoryForm();
        formBean.setStaffId(staffId);
        formBean.setFacilityId(facilityId);
        formBean.setMotechId(motechId);
        return formBean;
    }
}
