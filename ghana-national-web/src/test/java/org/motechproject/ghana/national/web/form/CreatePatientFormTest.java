package org.motechproject.ghana.national.web.form;

import org.junit.Test;
import org.motechproject.ghana.national.domain.RegistrationType;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreatePatientFormTest {
    @Test
    public void shouldCreateMrsPatientWithIdIfRegistrationModeIsUserDefined() {
        final CreatePatientForm form = new CreatePatientForm();
        form.setRegistrationMode(RegistrationType.USER_DEFINED);
        final String motechId = "12";
        form.setMotechId(motechId);
        assertThat(form.getPatient().mrsPatient().getId(), is(equalTo(motechId)));
    }

    @Test
    public void shouldNotCreateMrsPatientWithIdIfRegistrationModeIsUserDefined() {
        final CreatePatientForm form = new CreatePatientForm();
        form.setRegistrationMode(RegistrationType.AUTO_GENERATED);
        assertNull(form.getPatient().mrsPatient().getId());
    }
}
