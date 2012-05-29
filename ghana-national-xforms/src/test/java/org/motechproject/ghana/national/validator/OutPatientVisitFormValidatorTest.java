package org.motechproject.ghana.national.validator;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.*;

public class OutPatientVisitFormValidatorTest {

    OutPatientVisitFormValidator validator = new OutPatientVisitFormValidator();
    @Mock
    FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(validator, "formValidator", formValidator);
    }

    @Test
    public void shouldVerifyValidationCallsIfPatientIsAFemale() {
        OutPatientVisitForm formBean = mock(OutPatientVisitForm.class);

        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";

        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        Patient patient = null;
        final List<FormBean> formsUploaded = new ArrayList<FormBean>();
        formsUploaded.add(formBean);

        when(formValidator.getPatient(motechId)).thenReturn(patient);

        List<FormError> errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);

        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));

        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        // patient is dead
        patient = new Patient(new MRSPatient("motechId", new MRSPerson().dead(TRUE).gender("F"), new MRSFacility("facilityId")));
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)));

        patient.getMrsPatient().getPerson().gender("M");
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)));

        // registrant type is pregnant mother,patient is not female
        when(formBean.getRegistrantType()).thenReturn(PatientType.PREGNANT_MOTHER);
        patient.getMrsPatient().getPerson().dead(Boolean.FALSE).gender("M");
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG)));

        // patient not available in db, but form submit has reg client form
        when(formValidator.getPatient(motechId)).thenReturn(null);
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setSex("F");
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG))));
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND))));
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE))));

        // registrant type is child,age more than 5 years
        patient.getMrsPatient().getPerson().dead(Boolean.FALSE).age(6);
        when(formBean.getRegistrantType()).thenReturn(PatientType.CHILD_UNDER_FIVE);
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)));

        //patient exists in db,type is other
        when(formBean.getRegistrantType()).thenReturn(PatientType.OTHER);
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, not(hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG))));
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG))));

    }

}
