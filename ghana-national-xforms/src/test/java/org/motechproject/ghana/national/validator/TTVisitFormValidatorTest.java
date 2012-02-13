package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.TTVisitForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TTVisitFormValidatorTest {

    TTVisitFormValidator validator;

    @Mock
    FormValidator mockFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        validator = new TTVisitFormValidator();
        ReflectionTestUtils.setField(validator, "formValidator", mockFormValidator);

    }

    @Test
    public void shouldValidateIfFacility_PatientAndStaffAlreadyExists() {
        TTVisitForm form = setUpForm();

        FormError facilityIdError = new FormError("facilityId", "doest not exist");
        FormError staffIdError = new FormError("staffId", "doest not exist");
        FormError patientIdError = new FormError("patientId", "doest not exist");

        when(mockFormValidator.validateIfStaffExists(form.getStaffId())).thenReturn(Arrays.asList(staffIdError));
        when(mockFormValidator.validateIfFacilityExists(form.getFacilityId())).thenReturn(Arrays.asList(facilityIdError));
        when(mockFormValidator.validateIfPatientExistsAndIsAlive(form.getMotechId(), "motechId")).thenReturn(Arrays.asList(patientIdError));

        List<FormError> result = validator.validate(form);

        assertThat(result, hasItem(staffIdError));
        assertThat(result, hasItem(facilityIdError));
        assertThat(result, hasItem(patientIdError));

        when(mockFormValidator.validateIfStaffExists(form.getStaffId())).thenReturn(new ArrayList<FormError>());
        when(mockFormValidator.validateIfFacilityExists(form.getFacilityId())).thenReturn(new ArrayList<FormError>());
        when(mockFormValidator.validateIfPatientExistsAndIsAlive(form.getMotechId(), "motechId")).thenReturn(new ArrayList<FormError>());

        result = validator.validate(form);

        assertThat(result, not(hasItem(staffIdError)));
        assertThat(result, not(hasItem(facilityIdError)));
        assertThat(result, not(hasItem(patientIdError)));

    }

    @Test
    public void shouldValidateIfPatientAgeIsMoreThanTwelve() {
        TTVisitForm form = setUpForm();
        FormError ageError = new FormError("Patient age", "is less than 12");
        when(mockFormValidator.isAgeGreaterThan(eq(form.getMotechId()), anyInt())).thenReturn(false);
        List<FormError> result = validator.validate(form);
        assertThat(result, hasItem(ageError));

        when(mockFormValidator.isAgeGreaterThan(eq(form.getMotechId()), anyInt())).thenReturn(true);
        assertThat(validator.validate(form), not(hasItem(ageError)));
    }

    private TTVisitForm setUpForm() {
        TTVisitForm form = new TTVisitForm();
        String staffId = "staffId";
        String facilityId = "facilityId";
        String patientId = "patientId";
        form.setStaffId(staffId);
        form.setFacilityId(facilityId);
        form.setMotechId(patientId);
        return form;
    }
}
