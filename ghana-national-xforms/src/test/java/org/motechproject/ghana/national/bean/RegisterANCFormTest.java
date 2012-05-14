package org.motechproject.ghana.national.bean;

import org.junit.Test;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class RegisterANCFormTest {

    @Test
    public void shouldCreateMobileMidwifeEnrollment() {

        final String staffId = "staffId";
        final String facilityId = "facilityId";
        final MobileMidwifeBuilder enrollmentData = MobileMidwifeBuilder.builderWithSampleData(staffId, facilityId);
        final RegisterANCForm registerANCForm = spy(enrollmentData.buildRegisterANCForm(new RegisterANCForm()));
        registerANCForm.setMotechId("mid");
        registerANCForm.setConsent(true);
        registerANCForm.setEnroll(true);

        final MobileMidwifeEnrollment enrollment = registerANCForm.createMobileMidwifeEnrollment();
        assertThat(enrollment.getPatientId(), is("mid"));
        assertThat(enrollment.getFacilityId(), is(facilityId));
        assertThat(enrollment.getStaffId(), is(staffId));
        verify(registerANCForm).fillEnrollment(enrollment);

        assertNull(new RegisterANCForm().createMobileMidwifeEnrollment());
    }

}
