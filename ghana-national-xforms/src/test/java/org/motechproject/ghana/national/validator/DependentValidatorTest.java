package org.motechproject.ghana.national.validator;

import org.junit.Test;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.motechproject.ghana.national.domain.Constants.*;

public class DependentValidatorTest {

    @Test
    public void shouldNotReturnErrorIfAllValidationPasses() {
        Patient patient = new Patient(new MRSPatient("motechId", new MRSPerson().dead(FALSE).gender("F"), new MRSFacility("facilityId")));
        List<FormBean> formsUploaded = Collections.emptyList();

        final PatientValidator regClientFormValidators = new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForFemale());
        final PatientValidator validator = new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsFemale())).onFailure(regClientFormValidators);
        final List<FormError> errors = new DependentValidator().validate(patient, formsUploaded, validator);

        assertThat(errors.size(), is(0));
    }

    @Test
    public void shouldReturnErrorIfAnyOfTheValidatorsInTheChainFails() {
        Patient patient = null;
        List<FormBean> formsUploaded = new ArrayList<FormBean>();

        final PatientValidator regClientFormValidators = new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForFemale());
        final PatientValidator validator = new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsFemale())).onFailure(regClientFormValidators);
        List<FormError> errors = new DependentValidator().validate(patient, formsUploaded, validator);

        assertThat(errors, is(equalTo(Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)))));

        // patient is dead
        patient = new Patient(new MRSPatient("motechId", new MRSPerson().dead(TRUE).gender("F"), new MRSFacility("facilityId")));
        errors = new DependentValidator().validate(patient, formsUploaded, validator);
        assertThat(errors, is(equalTo(Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)))));

        patient.getMrsPatient().getPerson().gender("M");
        errors = new DependentValidator().validate(patient, formsUploaded, validator);
        assertThat(errors, is(equalTo(Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)))));

        // patient is not female
        patient.getMrsPatient().getPerson().dead(Boolean.FALSE).gender("M");
        errors = new DependentValidator().validate(patient, formsUploaded, validator);
        assertThat(errors, is(equalTo(Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG)))));


        // patient not available in db, but form submit has reg client form
        patient = null;
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setSex("F");
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);

        errors = new DependentValidator().validate(patient, formsUploaded, validator);
        assertThat(errors.size(), is(equalTo(0)));

        // reg client form has invalid gender
        registerClientForm.setSex("M");
        errors = new DependentValidator().validate(patient, formsUploaded, validator);
        assertThat(errors, is(equalTo(Arrays.asList(new FormError("Sex", "should be female")))));
    }
}
