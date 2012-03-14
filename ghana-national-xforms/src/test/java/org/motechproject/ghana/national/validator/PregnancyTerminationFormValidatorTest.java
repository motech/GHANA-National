package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PregnancyTerminationFormValidatorTest {
    private PregnancyTerminationFormValidator pregnancyTerminationFormValidator;
    @Mock
    private FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        pregnancyTerminationFormValidator = new PregnancyTerminationFormValidator();
        ReflectionTestUtils.setField(pregnancyTerminationFormValidator, "formValidator", formValidator);
    }

    @Test
    public void shouldTestValidate() throws Exception {
        String facilityId = "facilityId";
        String motechId = "motechId";
        String staffId = "staffId";
        PregnancyTerminationForm formBean = setUpForm(facilityId, motechId, staffId);
        when(formValidator.validateIfFacilityExists(facilityId)).thenReturn(Arrays.asList(new FormError("facility", "not found")));
        when(formValidator.validateIfStaffExists(staffId)).thenReturn(Arrays.asList(new FormError("staff", "not found")));
        when(formValidator.validatePatient(motechId, "motechId")).thenReturn(Arrays.asList(new FormError("motechId", "not found")));
        when(formValidator.validateIfPatientIsFemale(motechId, "motechId")).thenReturn(Arrays.asList(new FormError("motechId", "not female")));

        List<FormError> formErrors = pregnancyTerminationFormValidator.validate(formBean);

        assertThat(formErrors, hasItem(new FormError("facility", "not found")));
        assertThat(formErrors, hasItem(new FormError("staff", "not found")));
        assertThat(formErrors, hasItem(new FormError("motechId", "not found")));
        assertThat(formErrors, hasItem(new FormError("motechId", "not female")));
    }

    private PregnancyTerminationForm setUpForm(String facilityId, String motechId, String staffId) {
        PregnancyTerminationForm formBean = new PregnancyTerminationForm();
        formBean.setFacilityId(facilityId);
        formBean.setStaffId(staffId);
        formBean.setMotechId(motechId);
        return formBean;
    }
}
