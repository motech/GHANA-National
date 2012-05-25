package org.motechproject.ghana.national.validator.patient;

import org.junit.Test;
import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;

public class RegCWCFormSubmittedInSameUploadTest {
    @Test
    public void shouldValidatePatientRegisteredForCWCIfRegCWCFormSubmittedInSameUpload() {
        Patient patient = null;
        List<FormBean> formsUploaded = new ArrayList<FormBean>();
        final PatientValidator validator = new RegCWCFormSubmittedInSameUpload();

        List<FormError> formErrors = validator.validate(patient, formsUploaded);
        assertThat(formErrors,is(equalTo(Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "not registered for CWC")))));

        final RegisterCWCForm registerANCForm = new RegisterCWCForm();
        registerANCForm.setFormname("registerCWC");
        formsUploaded.add(registerANCForm);

        formErrors = validator.validate(patient, formsUploaded);
        assertThat(formErrors.size(), is(equalTo(0)));
    }
}
