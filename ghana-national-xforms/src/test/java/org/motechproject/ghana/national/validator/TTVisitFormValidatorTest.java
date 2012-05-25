package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.bean.TTVisitForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

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
        final String motechId = "motechId";

        TTVisitForm form = new TTVisitForm();
        form.setStaffId("staffId");
        form.setFacilityId("facilityId");
        form.setMotechId(motechId);

        List<FormError> errors = validator.validate(form, new FormBeanGroup(Collections.<FormBean>emptyList()));

        verify(mockFormValidator).validateIfStaffExists(eq("staffId"));
        verify(mockFormValidator).validateIfFacilityExists(eq("facilityId"));

        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        //patient exists in db, has age less than 12
        Patient patient = new Patient(new MRSPatient(motechId,new MRSPerson().age(10),new MRSFacility("facilityId")));
        doReturn(patient).when(mockFormValidator).getPatient(motechId);

        List<FormError> formErrors = validator.validate(form, new FormBeanGroup(Collections.<FormBean>emptyList()));
        assertThat(formErrors,hasItem(new FormError("Patient age", "is less than 12")));

        //patient exists in db,age greater than 12
        patient = new Patient(new MRSPatient(motechId,new MRSPerson().age(15),new MRSFacility("facilityId")));
        doReturn(patient).when(mockFormValidator).getPatient(motechId);

        formErrors = validator.validate(form, new FormBeanGroup(Collections.<FormBean>emptyList()));
        assertThat(formErrors,not(hasItem(new FormError("Patient age", "is less than 12"))));
        assertThat(formErrors,not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND))));

        //patient does not exist in db,reg client form not submitted
        doReturn(null).when(mockFormValidator).getPatient(motechId);
        formErrors = validator.validate(form, new FormBeanGroup(Collections.<FormBean>emptyList()));
        assertThat(formErrors,hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        //patient does not exist in db,reg client form submitted with age less than 12
        doReturn(null).when(mockFormValidator).getPatient(motechId);
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setDateOfBirth(DateUtil.today().minusYears(4).toDate());
        registerClientForm.setFormname("registerPatient");

        formErrors = validator.validate(form, new FormBeanGroup(Arrays.<FormBean>asList(registerClientForm)));

        assertThat(formErrors,hasItem(new FormError("Patient age", "is less than 12")));

        //patient does not exist in db,reg client form submitted with age more than 12
        doReturn(null).when(mockFormValidator).getPatient(motechId);
        registerClientForm.setDateOfBirth(DateUtil.today().minusYears(15).toDate());

        formErrors = validator.validate(form, new FormBeanGroup(Arrays.<FormBean>asList(registerClientForm)));

        assertThat(formErrors,not(hasItem(new FormError("Patient age", "is less than 12"))));

    }
}
