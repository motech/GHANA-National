package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class RegisterANCFormValidatorTest {
    private RegisterANCFormValidator registerANCFormValidator;

    @Mock
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Mock
    private MobileMidwifeValidator mockMobileMidwifeValidator;

    @Before
    public void setUp() {
        initMocks(this);
        registerANCFormValidator = new RegisterANCFormValidator();
        setField(registerANCFormValidator, "formValidator", formValidator);
        setField(registerANCFormValidator, "mobileMidwifeValidator", mockMobileMidwifeValidator);
    }

    @Test
    public void shouldValidatePatientDetailsWhileUploadingRegisterANCForm() {
        RegisterANCForm formBean = mock(RegisterANCForm.class);

        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";

        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        Patient patient = null;
        final List<FormBean> formsUploaded = new ArrayList<FormBean>();

        when(formValidator.getPatient(motechId)).thenReturn(patient);

        List<FormError> errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded));

        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
        verify(mockMobileMidwifeValidator, never()).validateForIncludeForm(Matchers.<MobileMidwifeEnrollment>any());

        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        // patient is dead
        patient = new Patient(new MRSPatient("motechId", new MRSPerson().dead(TRUE).gender("F"), new MRSFacility("facilityId")));
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded));
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)));

        patient.getMrsPatient().getPerson().gender("M");
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded));
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)));

        // patient is not female
        patient.getMrsPatient().getPerson().dead(Boolean.FALSE).gender("M");
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded));
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG)));


        // patient not available in db, but form submit has reg client form
        patient = null;
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setSex("F");
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);

        // reg client form has invalid gender
        registerClientForm.setSex("M");
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded));
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG)));
    }

    @Test
    public void shouldValidateMobileMidwifeIfEnrolledAlongWithANCForm() {
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        RegisterANCForm formBean = new MobileMidwifeBuilder().enroll(true).consent(false).facilityId(facilityId)
                .staffId(staffId).patientId(motechId).buildRegisterANCForm(new RegisterANCForm());

        registerANCFormValidator = spy(registerANCFormValidator);
        doReturn(emptyList()).when(registerANCFormValidator).validatePatient(Matchers.<Patient>any(), anyList());

        registerANCFormValidator.validate(formBean, new FormBeanGroup(Collections.<FormBean>emptyList()));

        ArgumentCaptor<MobileMidwifeEnrollment> captor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mockMobileMidwifeValidator).validateForIncludeForm(captor.capture());
        assertThat(captor.getValue().getStaffId(), is(org.hamcrest.Matchers.equalTo(staffId)));
        assertThat(captor.getValue().getPatientId(), is(org.hamcrest.Matchers.equalTo(motechId)));
        assertThat(captor.getValue().getFacilityId(), is(org.hamcrest.Matchers.equalTo(facilityId)));
    }
}
