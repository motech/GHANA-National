package org.motechproject.ghana.national.validator;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterCWCForm;
import org.motechproject.ghana.national.builders.MobileMidwifeBuilder;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
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
    public void setUp(){
        initMocks(this);
        registerCWCFormValidator = new RegisterCWCFormValidator();
        setField(registerCWCFormValidator, "formValidator", mockFormValidator);
        setField(registerCWCFormValidator, "patientService", mockPatientService);
        setField(registerCWCFormValidator, "mobileMidwifeValidator", mockMobileMidwifeValidator);
    }

    @Test
    public void shouldRaiseFormErrorIfMotechIdIsInvalid(){
        String motechId = "1234567";
        String staffId = "345";
        String facilityId = "1234";
        RegisterCWCForm registerCWCForm = setUpFormBean(facilityId, staffId,new Date(),"23232322",RegistrationToday.TODAY, motechId, Boolean.FALSE);
        List<FormError> formErrors = registerCWCFormValidator.validate(registerCWCForm);
        verify(mockFormValidator).validatePatient(eq(motechId), eq(Constants.MOTECH_ID_ATTRIBUTE_NAME));
        verify(mockFormValidator).validateIfStaffExists(eq(staffId));
        verify(mockFormValidator).validateIfFacilityExists(eq(facilityId));

        assertEquals(formErrors.size(), 1);
        assertThat(formErrors, hasItem(new FormError(Constants.MOTECH_ID_ATTRIBUTE_NAME, "is invalid")));
    }

    @Test
    public void shouldValidateCWCForm(){
        String motechId = "1234567";
        String staffId = "345";
        String facilityId = "1234";
        RegisterCWCForm registerCWCForm = setUpFormBean(facilityId, staffId,new Date(),"23232322",RegistrationToday.TODAY, motechId, Boolean.FALSE);

        registerCWCFormValidator.validate(registerCWCForm);

        verify(mockFormValidator).validatePatient(eq(motechId), eq(Constants.MOTECH_ID_ATTRIBUTE_NAME));
        verify(mockFormValidator).validateIfStaffExists(eq(staffId));
        verify(mockFormValidator).validateIfFacilityExists(eq(facilityId));
        verify(mockMobileMidwifeValidator, never()).validateForIncludeForm(any(MobileMidwifeEnrollment.class));
    }
    
    @Test
    public void shouldValidateMobileMidwifeIfEnrolledAlongWithCWCForm() {
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        RegisterCWCForm formBean = new MobileMidwifeBuilder().enroll(true).consent(false).facilityId(facilityId)
                .staffId(staffId).patientId(motechId).buildRegisterCWCForm(new RegisterCWCForm());

        registerCWCFormValidator = spy(registerCWCFormValidator);
        doReturn(emptyList()).when(registerCWCFormValidator).validatePatient(anyString());

        registerCWCFormValidator.validate(formBean);

        ArgumentCaptor<MobileMidwifeEnrollment> captor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        verify(mockMobileMidwifeValidator).validateForIncludeForm(captor.capture());
        assertThat(captor.getValue().getStaffId(), is(org.hamcrest.Matchers.equalTo(staffId)));
        assertThat(captor.getValue().getPatientId(), is(org.hamcrest.Matchers.equalTo(motechId)));
        assertThat(captor.getValue().getFacilityId(), is(org.hamcrest.Matchers.equalTo(facilityId)));
    }
    
    @Test
    public void shouldRaiseFormErrorIfChildAgeIsAboveFive(){
        String motechId = "1234567";
        when(mockPatientService.getAgeOfPatientByMotechId(motechId)).thenReturn(6);
        when(mockFormValidator.validatePatient(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME)).thenReturn(Collections.<FormError>emptyList());
        List<FormError> formErrors = registerCWCFormValidator.validatePatient(motechId);
        assertEquals(formErrors.size(), 1);
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
