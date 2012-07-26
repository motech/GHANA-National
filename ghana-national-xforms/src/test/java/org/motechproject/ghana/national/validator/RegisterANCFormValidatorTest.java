package org.motechproject.ghana.national.validator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.util.DateUtil;

import java.util.*;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
        RegisterANCForm formBean = new RegisterANCForm();

        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";

        formBean.setMotechId(motechId);
        formBean.setStaffId(staffId);
        formBean.setFacilityId(facilityId);


        Patient patient = null;
        List<FormBean> formsUploaded = new ArrayList<FormBean>();

        when(formValidator.getPatient(motechId)).thenReturn(patient);

        List<FormError> errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);

        verify(formValidator).validateIfStaffExists(eq(staffId));
        verify(formValidator).validateIfFacilityExists(eq(facilityId));
        verify(mockMobileMidwifeValidator, never()).validateTime(Matchers.<MobileMidwifeEnrollment>any());

        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        // patient is dead
        patient = new Patient(new MRSPatient("motechId", new MRSPerson().dead(TRUE).gender("F").dateOfBirth(DateUtil.newDate(2000, 12, 12).toDate()), new MRSFacility("facilityId")));
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)));

        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE)));

        // patient is not female
        patient.getMrsPatient().getPerson().dead(Boolean.FALSE).gender("M");
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG)));

        //historyDates are before DOB of patient when available in db
        formBean.setAddHistory(true);
        patient.getMrsPatient().getPerson().gender("F");

        formBean.setLastIPTDate(DateUtil.newDate(2000, 12, 1).toDate());
        formBean.setLastTTDate(DateUtil.newDate(2000, 12, 1).toDate());

        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError("lastIPTDate", Constants.AFTER_DOB)));
        assertThat(errors, hasItem(new FormError("lastTTDate", Constants.AFTER_DOB)));

        //historyDates are after DOB of patient when available in db
        formBean.setLastIPTDate(DateUtil.newDate(2000, 12, 25).toDate());
        formBean.setLastTTDate(DateUtil.newDate(2000, 12, 25).toDate());

        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, not(hasItem(new FormError("lastIPTDate", Constants.AFTER_DOB))));
        assertThat(errors, not(hasItem(new FormError("lastTTDate", Constants.AFTER_DOB))));

        // patient not available in db, but form submit has reg client form
        patient = null;
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setSex("F");
        registerClientForm.setFormname("registerPatient");
        formsUploaded.add(registerClientForm);

        // reg client form has invalid gender
        registerClientForm.setSex("M");
        registerClientForm.setDateOfBirth(DateUtil.newDate(2000, 12, 12).toDate());
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError("Sex", GENDER_ERROR_MSG)));

        //historyDates are after DOB of patient when form uploaded with regANC
        patient = null;
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        registerClientForm.setDateOfBirth(DateUtil.newDate(2000, 12, 12).toDate());
        registerClientForm.setSex("F");
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setRegistrantType(PatientType.PREGNANT_MOTHER);
        formsUploaded = new ArrayList<FormBean>();
        formsUploaded.add(registerClientForm);
        formBean.setAddHistory(true);

        //historyDates are before DOB of patient when available in db
        formBean.setLastIPTDate(DateUtil.newDate(2000, 12, 1).toDate());
        formBean.setLastTTDate(DateUtil.newDate(2000, 12, 1).toDate());
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, hasItem(new FormError("lastIPTDate", Constants.AFTER_DOB)));
        assertThat(errors, hasItem(new FormError("lastTTDate", Constants.AFTER_DOB)));


        //historyDates are after DOB of patient when available in db
        formBean.setLastIPTDate(DateUtil.newDate(2000, 12, 25).toDate());
        formBean.setLastTTDate(DateUtil.newDate(2000, 12, 25).toDate());
        errors = registerANCFormValidator.validate(formBean, new FormBeanGroup(formsUploaded), formsUploaded);
        assertThat(errors, not(hasItem(new FormError("lastIPTDate", Constants.AFTER_DOB))));
        assertThat(errors, not(hasItem(new FormError("lastTTDate", Constants.AFTER_DOB))));
    }

    @Test
    public void shouldValidateMobileMidwifeIfEnrolledAlongWithANCForm() {
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        RegisterANCForm formBean = new MobileMidwifeBuilder().enroll(true).consent(false).facilityId(facilityId)
                .staffId(staffId).patientId(motechId).buildRegisterANCForm(new RegisterANCForm());
        formBean.setAddHistory(false);
        registerANCFormValidator = spy(registerANCFormValidator);
        Patient patient = mock(Patient.class);
        when(patient.dateOfBirth()).thenReturn(DateTime.now());
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        doReturn(emptyList()).when(registerANCFormValidator).validatePatient(eq(patient), eq(formBean), anyList(), anyList());

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean);
        registerANCFormValidator.validate(formBean, new FormBeanGroup(formBeans), formBeans);

        ArgumentCaptor<MobileMidwifeEnrollment> captor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mockMobileMidwifeValidator).validateTime(captor.capture());
        assertThat(captor.getValue().getStaffId(), is(org.hamcrest.Matchers.equalTo(staffId)));
        assertThat(captor.getValue().getPatientId(), is(org.hamcrest.Matchers.equalTo(motechId)));
        assertThat(captor.getValue().getFacilityId(), is(org.hamcrest.Matchers.equalTo(facilityId)));
    }
}
