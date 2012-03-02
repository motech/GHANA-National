package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class ClientQueryFormValidatorTest {
    private ClientQueryFormValidator clientQueryFormValidator;
    @Mock
    private FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        clientQueryFormValidator = new ClientQueryFormValidator();
        ReflectionTestUtils.setField(clientQueryFormValidator, "formValidator", formValidator);
    }

    @Test
    public void shouldValidateTheClientQueryFormBean() {
        String facilityId = "facilityId";
        String motechId = "motechId";
        String staffId = "staffId";
        ClientQueryForm clientQueryForm = createClientQueryForm(facilityId, motechId, staffId);

        when(formValidator.validateIfFacilityExists(facilityId)).thenReturn(asList(new FormError(facilityId, NOT_FOUND)));
        when(formValidator.validatePatient(motechId, motechId)).thenReturn(asList(new FormError(motechId, NOT_FOUND)));
        when(formValidator.validateIfStaffExists(staffId)).thenReturn(asList(new FormError(staffId, NOT_FOUND)));

        List<FormError> formErrors = clientQueryFormValidator.validate(clientQueryForm);

        assertThat(formErrors, hasItem(new FormError(motechId, NOT_FOUND)));
        assertThat(formErrors, hasItem(new FormError(facilityId, NOT_FOUND)));
        assertThat(formErrors, hasItem(new FormError(staffId, NOT_FOUND)));
    }

    private ClientQueryForm createClientQueryForm(String facilityId, String motechId, String staffId) {
        ClientQueryForm clientQueryForm = new ClientQueryForm();
        clientQueryForm.setFacilityId(facilityId);
        clientQueryForm.setMotechId(motechId);
        clientQueryForm.setStaffId(staffId);
        return clientQueryForm;
    }
}
