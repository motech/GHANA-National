package org.motechproject.ghana.national.validator;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.mapper.OutPatientVisitMapper;
import org.motechproject.ghana.national.repository.AllMotechModuleOutPatientVisits;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_MORE_ERR_MSG;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_PARAMETER;
import static org.motechproject.ghana.national.domain.Constants.GENDER_ERROR_MSG;
import static org.motechproject.ghana.national.domain.Constants.IS_DUPLICATE;
import static org.motechproject.ghana.national.domain.Constants.IS_NOT_ALIVE;
import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class OutPatientVisitFormValidatorTest {

    @InjectMocks
    OutPatientVisitFormValidator validator = new OutPatientVisitFormValidator();
    @Mock
    FormValidator mockFormValidator;
    @Mock
    AllMotechModuleOutPatientVisits mockAllMotechModuleOutPatientVisits;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldVerifyValidationCallsIfPatientIsAFemale() {
        OutPatientVisitForm formBean = mock(OutPatientVisitForm.class);

        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        int diagnosis = 1;
        String serialNumber = "serialNumber";
        Date dateOfBirth = new Date();

        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);
        when(formBean.getDiagnosis()).thenReturn(diagnosis);
        when(formBean.getSerialNumber()).thenReturn(serialNumber);
        when(formBean.getDateOfBirth()).thenReturn(dateOfBirth);
        when(formBean.isRegistered()).thenReturn(true);

        Patient patient = null;
        final List<FormBean> formsUploaded = new ArrayList<FormBean>();
        formsUploaded.add(formBean);

        when(mockFormValidator.getPatient(motechId)).thenReturn(patient);

        List<FormError> errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);

        verify(mockFormValidator).validateIfStaffExists(eq(staffId));
        verify(mockFormValidator).validateIfFacilityExists(eq(facilityId));

        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        // patient is dead
        patient = new Patient(new MRSPatient("motechId", new MRSPerson().dead(TRUE).gender("F"), new MRSFacility("facilityId")));
        when(mockFormValidator.getPatient(motechId)).thenReturn(patient);
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
        when(mockFormValidator.getPatient(motechId)).thenReturn(null);
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
        when(mockFormValidator.getPatient(motechId)).thenReturn(patient);
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)));

        //patient exists in db,type is other
        when(formBean.getRegistrantType()).thenReturn(PatientType.OTHER);
        when(mockFormValidator.getPatient(motechId)).thenReturn(patient);
        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, not(hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG))));
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG))));

        //patient is duplicate
        when(formBean.getRegistrantType()).thenReturn(PatientType.OTHER);
        when(formBean.getVisitDate()).thenReturn(new Date());
        when(formBean.isRegistered()).thenReturn(false);
        when(mockAllMotechModuleOutPatientVisits.isDuplicate(new OutPatientVisitMapper().map(formBean))).thenReturn(true);
        when(mockFormValidator.validateIfFacilityExists(facilityId)).thenReturn(Collections.<FormError>emptyList());
        when(mockFormValidator.validateIfStaffExists(staffId)).thenReturn(Collections.<FormError>emptyList());

        errors = validator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);

        assertThat(errors, hasItem(new FormError("OPD Form", IS_DUPLICATE)));

    }

}
