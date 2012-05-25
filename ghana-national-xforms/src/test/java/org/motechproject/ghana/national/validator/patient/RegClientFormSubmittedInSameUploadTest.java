package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RegClientFormSubmittedInSameUploadTest {
    @Test
    public void shouldValidateIfPatientExistsIfRegClientFormSubmittedInSameUpload() {
        Patient patient = null;
        List<FormBean> formsUploaded = new ArrayList<FormBean>();
        final PatientValidator validator = new RegClientFormSubmittedInSameUpload();

        List<FormError> formErrors = validator.validate(patient, formsUploaded);
        assertThat(formErrors,is(equalTo(Arrays.asList(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME,Constants.NOT_FOUND)))));

        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);

        formErrors = validator.validate(patient, formsUploaded);
        assertThat(formErrors.size(), is(equalTo(0)));
    }
}
