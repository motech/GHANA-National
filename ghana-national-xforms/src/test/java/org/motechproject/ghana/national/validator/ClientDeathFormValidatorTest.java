package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.validator.patient.ExistsInDb;
import org.motechproject.ghana.national.validator.patient.PatientValidator;
import org.motechproject.ghana.national.validator.patient.RegClientFormSubmittedInSameUpload;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClientDeathFormValidatorTest {
    private ClientDeathFormValidator clientDeathFormValidator;
    @Mock
    private FormValidator mockFormValidator;
    @Mock
    private PatientService mockPatientService;
    
    @Before
    public void setUp() {
        initMocks(this);
        clientDeathFormValidator = spy(new ClientDeathFormValidator());
        ReflectionTestUtils.setField(clientDeathFormValidator, "formValidator", mockFormValidator);
    }
    
    @Test
    public void shouldValidatePatientId() {
        ClientDeathForm clientDeathForm = new ClientDeathForm();
        clientDeathForm.setDate(DateUtil.now().toDate());
        clientDeathForm.setFacilityId("facilityId");
        clientDeathForm.setMotechId("motechId");
        clientDeathForm.setStaffId("staffId");
        List<FormBean> formBeans = Collections.emptyList();

        PatientValidator expectedValidators = new ExistsInDb().onFailure(new RegClientFormSubmittedInSameUpload());
        Patient patient = new Patient();
        when(mockFormValidator.getPatient("motechId")).thenReturn(patient);
        final DependentValidator mockDependentValidator = mock(DependentValidator.class);
        when(clientDeathFormValidator.dependentValidator()).thenReturn(mockDependentValidator);
        List<FormError> formErrors = clientDeathFormValidator.validate(clientDeathForm, new FormBeanGroup(formBeans));

        assertFalse(formErrors.isEmpty());
        assertNotNull(select(formErrors, having(on(FormError.class).getParameter(), is(equalTo("motechId")))));
        assertNotNull(select(formErrors, having(on(FormError.class).getParameter(), is(equalTo("staffId")))));
        assertNotNull(select(formErrors, having(on(FormError.class).getParameter(), is(equalTo("facilityId")))));
        verify(mockDependentValidator).validate(patient, formBeans, expectedValidators);
    }

}
