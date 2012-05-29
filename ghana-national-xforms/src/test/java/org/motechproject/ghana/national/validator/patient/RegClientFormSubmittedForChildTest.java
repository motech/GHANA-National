package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RegClientFormSubmittedForChildTest {
    @Test
    public void shouldValidateIfPatientIsFemaleIfRegClientFormSubmittedInSameUpload() {
        Patient patient = null;
        List<FormBean> formsUploaded = new ArrayList<FormBean>();
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);

        final PatientValidator validator = new RegClientFormSubmittedForChild();
        List<FormError> errors = validator.validate(patient, formsUploaded, formsUploaded);
        assertThat(errors.size(), is(equalTo(0)));

        registerClientForm.setRegistrantType(PatientType.OTHER);
        errors = validator.validate(patient,formsUploaded, formsUploaded);
        assertThat(errors,is(equalTo(Arrays.asList(new FormError("Client Type", "should be a child")))));
    }
}

