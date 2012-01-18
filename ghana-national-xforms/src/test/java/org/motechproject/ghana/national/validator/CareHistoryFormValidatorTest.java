package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CareHistoryForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CareHistoryFormValidatorTest {
    private CareHistoryFormValidator careHistoryFormValidator;
    @Mock
    private FormValidator mockFormValidator;
    @Mock
    private PatientService patientService;

    @Before
    public void setUp() {
        initMocks(this);
        careHistoryFormValidator = new CareHistoryFormValidator();
        ReflectionTestUtils.setField(careHistoryFormValidator, "formValidator", mockFormValidator);
        ReflectionTestUtils.setField(careHistoryFormValidator, "patientService", patientService);
    }

    @Test
    public void shouldValidateIfCareHistoryFormBeanIsValid() {
        String staffId = "1234567";
        String facilityId = "098765";
        String motechId = "0234567";

        CareHistoryForm formBean = careHistoryFormBean(staffId, facilityId, motechId);
        when(patientService.getPatientByMotechId(motechId)).thenReturn(new Patient());

        List<FormError> formErrors = careHistoryFormValidator.validate(formBean);
        assertFalse(formErrors.isEmpty());
        assertFalse(select(formErrors, having(on(FormError.class).getParameter(), is("staffId"))).isEmpty());
        assertFalse(select(formErrors, having(on(FormError.class).getParameter(), is("facilityId"))).isEmpty());
        assertFalse(select(formErrors, having(on(FormError.class).getParameter(), is("motechId"))).isEmpty());

        verify(mockFormValidator).validateIfFacilityExists(facilityId);
        verify(mockFormValidator).validateIfStaffExists(staffId);
        verify(mockFormValidator).validatePatient(motechId, "motechId");
    }

    private CareHistoryForm careHistoryFormBean(String staffId, String facilityId, String motechId) {
        CareHistoryForm formBean = new CareHistoryForm();
        formBean.setStaffId(staffId);
        formBean.setFacilityId(facilityId);
        formBean.setMotechId(motechId);
        return formBean;
    }
}
