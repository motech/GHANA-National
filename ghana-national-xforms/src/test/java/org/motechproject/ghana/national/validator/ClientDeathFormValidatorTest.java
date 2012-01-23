package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
        clientDeathFormValidator = new ClientDeathFormValidator();
        ReflectionTestUtils.setField(clientDeathFormValidator, "formValidator", mockFormValidator);
        ReflectionTestUtils.setField(clientDeathFormValidator, "patientService", mockPatientService);
    }
    
    @Test
    public void shouldValidatePatientId() {
        List<FormError> formErrors = clientDeathFormValidator.validate(clientDeathForm());
        assertFalse(formErrors.isEmpty());
        assertNotNull(select(formErrors, having(on(FormError.class).getParameter(), is(equalTo("motechId")))));
        assertNotNull(select(formErrors, having(on(FormError.class).getParameter(), is(equalTo("staffId")))));
        assertNotNull(select(formErrors, having(on(FormError.class).getParameter(), is(equalTo("facilityId")))));

    }

    private ClientDeathForm clientDeathForm() {
        ClientDeathForm clientDeathForm = new ClientDeathForm();
        clientDeathForm.setDate(DateUtil.now().toDate());
        clientDeathForm.setFacilityId("facilityId");
        clientDeathForm.setMotechId("motechId");
        clientDeathForm.setStaffId("staffId");
        return clientDeathForm;
    }
}
