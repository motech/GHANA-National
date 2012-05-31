package org.motechproject.ghana.national.validator;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class RegisterCWCFormValidatorTest {

    private RegisterCWCFormValidator registerCWCFormValidator;

    @Mock
    private FormValidator mockFormValidator;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private MobileMidwifeValidator mockMobileMidwifeValidator;

    @Before
    public void setUp() {
        initMocks(this);
        registerCWCFormValidator = new RegisterCWCFormValidator();
        setField(registerCWCFormValidator, "formValidator", mockFormValidator);
        setField(registerCWCFormValidator, "patientService", mockPatientService);
        setField(registerCWCFormValidator, "mobileMidwifeValidator", mockMobileMidwifeValidator);
    }


    @Test
    public void shouldValidateCWCForm() {
        String motechId = "1234567";
        String staffId = "345";
        String facilityId = "1234";
        RegisterCWCForm registerCWCForm = setUpFormBean(facilityId, staffId, new Date(), "23232322", RegistrationToday.TODAY, motechId, Boolean.FALSE);

        List<FormBean> formBeans = Arrays.<FormBean>asList(registerCWCForm);
        List<FormError> errors = registerCWCFormValidator.validate(registerCWCForm, new FormBeanGroup(formBeans), formBeans);

        verify(mockFormValidator).validateIfStaffExists(eq(staffId));
        verify(mockFormValidator).validateIfFacilityExists(eq(facilityId));
        verify(mockMobileMidwifeValidator, never()).validateTime(any(MobileMidwifeEnrollment.class));
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        // patient is not child
        Patient patient = new Patient(new MRSPatient(motechId,new MRSPerson().dead(false).age(6),new MRSFacility(facilityId)));
        doReturn(patient).when(mockFormValidator).getPatient(motechId);
        formBeans = Arrays.<FormBean>asList(registerCWCForm);
        errors = registerCWCFormValidator.validate(registerCWCForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(errors, hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)));

        // patient not in db, reg client form not submitted
        doReturn(null).when(mockFormValidator).getPatient(motechId);
        formBeans = Arrays.<FormBean>asList(registerCWCForm);
        errors = registerCWCFormValidator.validate(registerCWCForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        // patient not available in db, but form submit has reg client form
        final RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setRegistrantType(PatientType.CHILD_UNDER_FIVE);

        formBeans = Arrays.<FormBean>asList(registerClientForm);
        errors = registerCWCFormValidator.validate(registerCWCForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND))));

        // reg client form submitted with wrong type
        registerClientForm.setRegistrantType(PatientType.PREGNANT_MOTHER);
        formBeans = Arrays.<FormBean>asList(registerClientForm);
        errors = registerCWCFormValidator.validate(registerCWCForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(errors, hasItem(new FormError("patient type", "cannot be " + registerClientForm.getRegistrantType())));
        
        //reg client form submitted for patient type other, age more than 5
        registerClientForm.setRegistrantType(PatientType.OTHER);
        registerClientForm.setDateOfBirth(DateUtil.newDate(2000,1,1).toDate());
        errors = registerCWCFormValidator.validate(registerCWCForm,new FormBeanGroup(formBeans), formBeans);
        assertThat(errors,hasItem(new FormError("Patient age", "is more than 5")));
    }

    @Test
    public void shouldValidateMobileMidwifeIfEnrolledAlongWithCWCForm() {
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        final RegisterCWCForm registerCWCForm = new RegisterCWCForm();
        registerCWCForm.setRegistrationDate(DateUtil.now().toDate());
        RegisterCWCForm formBean = new MobileMidwifeBuilder().enroll(true).consent(false).facilityId(facilityId)
                .staffId(staffId).patientId(motechId).buildRegisterCWCForm(registerCWCForm);

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean);
        registerCWCFormValidator.validate(formBean, new FormBeanGroup(formBeans), formBeans);

        ArgumentCaptor<MobileMidwifeEnrollment> captor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mockMobileMidwifeValidator).validateTime(captor.capture());
        assertThat(captor.getValue().getStaffId(), is(org.hamcrest.Matchers.equalTo(staffId)));
        assertThat(captor.getValue().getPatientId(), is(org.hamcrest.Matchers.equalTo(motechId)));
        assertThat(captor.getValue().getFacilityId(), is(org.hamcrest.Matchers.equalTo(facilityId)));
    }

    private RegisterCWCForm setUpFormBean(String facilityId, String staffId, Date date, String serialNumber, RegistrationToday registrationToday, String motechId, Boolean consent) {
        RegisterCWCForm registerCWCForm = new RegisterCWCForm();
        registerCWCForm.setFacilityId(facilityId);
        registerCWCForm.setStaffId(staffId);
        registerCWCForm.setRegistrationDate(new Date());
        registerCWCForm.setSerialNumber(serialNumber);
        registerCWCForm.setRegistrationToday(registrationToday);
        registerCWCForm.setMotechId(motechId);
        registerCWCForm.setAddHistory(true);
        registerCWCForm.setAddCareHistory(StringUtils.join(CwcCareHistory.values(), " "));
        registerCWCForm.setConsent(consent);
        return registerCWCForm;
    }
}
