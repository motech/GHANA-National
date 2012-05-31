package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class RegClientFormSubmittedForPatientWithAgeMoreThanTest {
    @Test
    public void shouldValidateAgeOfPatientMoreThanExpected(){
        Patient patient = null;
        List<FormBean> formsUploaded = new ArrayList<FormBean>();
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setDateOfBirth(DateUtil.newDate(2000, 1, 1).toDate());
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);

        final PatientValidator validator = new RegClientFormSubmittedForPatientWithAgeMoreThan(12);
        List<FormError> errors = validator.validate(patient, formsUploaded, formsUploaded);
        assertThat(errors.size(), is(equalTo(0)));

        registerClientForm.setDateOfBirth(DateUtil.today().minusYears(10).toDate());
        errors = validator.validate(patient, formsUploaded, formsUploaded);
        assertThat(errors, is(equalTo(Arrays.asList(new FormError("Patient age", "is less than 12")))));

    }
}
