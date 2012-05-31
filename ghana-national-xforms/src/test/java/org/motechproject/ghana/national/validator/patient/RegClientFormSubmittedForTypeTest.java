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

public class RegClientFormSubmittedForTypeTest {
    @Test
    public void shouldValidateTypeOfPatient() {
        Patient patient = null;
        List<FormBean> formsUploaded = new ArrayList<FormBean>();
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);

        PatientValidator validator = new RegClientFormSubmittedForType(PatientType.CHILD_UNDER_FIVE);
        List<FormError> errors = validator.validate(patient, formsUploaded, formsUploaded);
        assertThat(errors.size(), is(equalTo(0)));

        registerClientForm.setRegistrantType(PatientType.PREGNANT_MOTHER);
        validator = new RegClientFormSubmittedForType(PatientType.OTHER);
        errors = validator.validate(patient,formsUploaded, formsUploaded);
        assertThat(errors,is(equalTo(Arrays.asList(new FormError("patient type", "cannot be "+ PatientType.PREGNANT_MOTHER)))));
    }
}
