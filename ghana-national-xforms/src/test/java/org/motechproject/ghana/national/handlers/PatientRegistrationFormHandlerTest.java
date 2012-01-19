package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.ANCService;
import org.motechproject.ghana.national.service.CWCService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientRegistrationFormHandlerTest {

    @Mock
    PatientService mockPatientService;
    @Mock
    FacilityService mockFacilityService;

    @Mock
    CWCService mockCWCService;

    PatientRegistrationFormHandler patientRegistrationFormHandler;
    @Mock
    ANCService mockANCService;


    @Before
    public void setUp() {
        initMocks(this);
        patientRegistrationFormHandler = new PatientRegistrationFormHandler();
        ReflectionTestUtils.setField(patientRegistrationFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(patientRegistrationFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(patientRegistrationFormHandler, "cwcService", mockCWCService);
        ReflectionTestUtils.setField(patientRegistrationFormHandler, "ancService", mockANCService);
    }

    @Test
    public void shouldHandleRegisterClientEventAndInvokeService() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        String address = "Address";
        Date dateofBirth = new Date(10, 10, 2011);
        String district = "District";
        Boolean isBirthDateEstimated = true;
        String motechFacilityId = "MotechFacilityID";
        String facilityId = "Facility Id";
        String firstName = "FirstName";
        Boolean insured = true;
        String lastName = "LastName";
        String middleName = "MiddleName";
        String motechId = "motechId";
        Date nhisExpDate = new Date(10, 10, 2022);
        String nhisNumber = "NhisNumber";
        String parentId = "ParentId";
        String preferredName = "PreferredName";
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        String phoneNumber = "0123456789";
        final String staffId = "456";
        PatientType patientType = PatientType.CHILD_UNDER_FIVE;

        RegisterClientForm registerClientForm = createRegisterClientForm(address, dateofBirth, district, isBirthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId, preferredName, region, registrationMode, sex, subDistrict, phoneNumber, patientType, "defaultV");
        parameters.put(PatientRegistrationFormHandler.FORM_BEAN, registerClientForm);
        MotechEvent event = new MotechEvent("subject", parameters);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        doReturn(motechId).when(mockPatientService).registerPatient(patientArgumentCaptor.capture());

        Facility facility = mock(Facility.class);
        when(facility.mrsFacilityId()).thenReturn(facilityId);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);

        patientRegistrationFormHandler.handleFormEvent(event);

        Patient savedPatient = patientArgumentCaptor.getValue();
        MRSPerson mrsPerson = savedPatient.getMrsPatient().getPerson();
        assertRegisterPatient(address, dateofBirth, isBirthDateEstimated, facilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, preferredName, sex, phoneNumber, savedPatient, mrsPerson);
    }

    @Test
    public void shouldBeRegisteredAsAListenerForRegisterPatientEvent() throws NoSuchMethodException {
        String[] registeredEventSubject = patientRegistrationFormHandler.getClass().getMethod("handleFormEvent", new Class[]{MotechEvent.class}).getAnnotation(MotechListener.class).subjects();
        assertThat(registeredEventSubject, is(equalTo(new String[]{"form.validation.successful.NurseDataEntry.registerPatient"})));
    }

    @Test
    public void shouldRunAsAdminUser() throws NoSuchMethodException {
        assertThat(patientRegistrationFormHandler.getClass().getMethod("handleFormEvent", new Class[]{MotechEvent.class}).getAnnotation(LoginAsAdmin.class), is(not(equalTo(null))));
    }

    @Test
    public void shouldRegisterForCWC() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        String address = "Address";
        Date dateofBirth = new Date(10, 10, 2011);
        String district = "District";
        Boolean isBirthDateEstimated = true;
        String motechFacilityId = "MotechFacilityID";
        String firstName = "FirstName";
        Boolean insured = true;
        String lastName = "LastName";
        String middleName = "MiddleName";
        String motechId = "motechId";
        Date nhisExpDate = new Date(10, 10, 2022);
        String nhisNumber = "NhisNumber";
        String parentId = "ParentId";
        String preferredName = "PreferredName";
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        String phoneNumber = "0123456789";
        PatientType patientType = PatientType.CHILD_UNDER_FIVE;

        final Date registartionDate = new Date(2011, 9, 1);
        final Date lastBCGDate = new Date(2011, 10, 1);
        final Date lastVitADate = new Date(2011, 11, 1);
        final Date lastMeaslesDate = new Date(2011, 9, 2);
        final Date lastYfDate = new Date(2011, 9, 3);
        final Date lastPentaDate = new Date(2011, 9, 4);
        final Date lastOPVDate = new Date(2011, 9, 5);
        final Date lastIPTiDate = new Date(2011, 9, 6);
        final String staffId = "456";
        final int lastPenta = 1;
        final int lastOPV = 0;
        final int lastIPTi = 1;


        RegisterClientForm registerClientForm = createRegisterClientFormForCWCEnrollment(address, dateofBirth, district, isBirthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId,
                preferredName, region, registrationMode, sex, subDistrict, phoneNumber, patientType, registartionDate,
                lastBCGDate, lastVitADate, lastMeaslesDate, lastYfDate, lastPentaDate, lastOPVDate, lastIPTiDate, staffId, lastPenta, lastOPV, lastIPTi);

        parameters.put(PatientRegistrationFormHandler.FORM_BEAN, registerClientForm);
        MotechEvent event = new MotechEvent("subject", parameters);

        Facility facility = mock(Facility.class);
        when(facility.mrsFacilityId()).thenReturn(motechFacilityId);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);

        when(mockCWCService.enroll(any(CwcVO.class), anyString())).thenReturn(null);

        when(mockPatientService.registerPatient(any(Patient.class))).thenReturn(motechId);
        when(facility.mrsFacilityId()).thenReturn(motechFacilityId);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);
        patientRegistrationFormHandler.handleFormEvent(event);
        final ArgumentCaptor<CwcVO> captor = ArgumentCaptor.forClass(CwcVO.class);
        verify(mockCWCService).enroll(captor.capture(), eq(Constants.REGCLIENTCWC));
        final CwcVO cwcVO = captor.getValue();

        assertCWCRegistration(motechFacilityId, motechId, registartionDate, lastBCGDate, lastVitADate, lastMeaslesDate, lastYfDate, lastPentaDate, lastOPVDate, lastIPTiDate, staffId, lastPenta, lastOPV, cwcVO);

    }

    @Test
    public void shouldRegisterForANC() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {

        HashMap<String, Object> parameters = new HashMap<String, Object>();

        String address = "Address";
        Date dateofBirth = new Date(10, 10, 2011);
        String district = "District";
        Boolean isBirthDateEstimated = true;
        String motechFacilityId = "MotechFacilityID";
        String firstName = "FirstName";
        Boolean insured = true;
        String lastName = "LastName";
        String middleName = "MiddleName";
        String motechId = "motechId";
        Date nhisExpDate = new Date(10, 10, 2022);
        String nhisNumber = "NhisNumber";
        String parentId = "ParentId";
        String preferredName = "PreferredName";
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        String phoneNumber = "0123456789";
        PatientType patientType = PatientType.PREGNANT_MOTHER;
        final String staffId = "456";


        Date expDeliveryDate = new Date(10, 12, 2012);
        Boolean deliveryDateConfirmed = true;
        Double height = 5.5;
        Integer gravida = 3;
        Integer parity = 1;
        Date lastIPTDate = new Date(10, 12, 2000);
        Date lastTTDate = new Date(10, 12, 1999);
        String lastIPT = "lastIPT";
        String lastTT = "lastTT";

        RegisterClientForm registerClientForm = createRegisterClientFormForANCEnrollment(address, dateofBirth, district, isBirthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId,
                preferredName, region, registrationMode, sex, subDistrict, phoneNumber, patientType, expDeliveryDate, deliveryDateConfirmed, height, gravida, parity, lastIPTDate, lastTTDate, lastIPT, lastTT, staffId);

        parameters.put(PatientRegistrationFormHandler.FORM_BEAN, registerClientForm);
        MotechEvent event = new MotechEvent("subject", parameters);

        Facility facility = mock(Facility.class);
        when(facility.mrsFacilityId()).thenReturn(motechFacilityId);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);

        when(mockANCService.enroll(any(ANCVO.class), anyString())).thenReturn(null);
        when(mockPatientService.registerPatient(any(Patient.class))).thenReturn(motechId);

        when(facility.mrsFacilityId()).thenReturn(motechFacilityId);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);
        patientRegistrationFormHandler.handleFormEvent(event);
        final ArgumentCaptor<ANCVO> captor = ArgumentCaptor.forClass(ANCVO.class);
        verify(mockANCService).enroll(captor.capture(), eq(Constants.REGCLIENTANC));
        final ANCVO ancVO = captor.getValue();

        assertANCRegistration(motechFacilityId, motechId, expDeliveryDate, deliveryDateConfirmed, height, gravida, parity, lastIPTDate, lastTTDate, lastIPT, lastTT, ancVO);

    }

    private void assertANCRegistration(String motechFacilityId, String motechId, Date expDeliveryDate, Boolean deliveryDateConfirmed, Double height, Integer gravida, Integer parity, Date lastIPTDate, Date lastTTDate, String lastIPT, String lastTT, ANCVO ancVO) {
        assertThat(motechFacilityId, is(ancVO.getFacilityId()));
        assertThat(motechId, is(ancVO.getMotechPatientId()));
        assertThat(expDeliveryDate, is(ancVO.getEstimatedDateOfDelivery()));
        assertThat(deliveryDateConfirmed, is(ancVO.getDeliveryDateConfirmed()));
        assertThat(height, is(ancVO.getHeight()));
        assertThat(gravida, is(ancVO.getGravida()));
        assertThat(parity, is(ancVO.getParity()));
        assertThat(lastIPTDate, is(ancVO.getLastIPTDate()));
        assertThat(lastTTDate, is(ancVO.getLastTTDate()));
        assertThat(lastIPT, is(ancVO.getLastIPT()));
        assertThat(lastTT, is(ancVO.getLastTT()));
    }


    private RegisterClientForm createRegisterClientFormForCWCEnrollment(String address, Date dateofBirth, String district, Boolean birthDateEstimated, String motechFacilityId, String firstName, Boolean insured, String lastName, String middleName, String motechId, Date nhisExpDate, String nhisNumber, String parentId, String preferredName, String region, RegistrationType registrationMode, String sex, String subDistrict, String phoneNumber, PatientType patientType, Date registartionDate, Date lastBCGDate, Date lastVitADate, Date lastMeaslesDate, Date lastYfDate, Date lastPentaDate, Date lastOPVDate, Date lastIPTiDate, String staffId, int lastPenta, int lastOPV, int lastIPTi) {
        RegisterClientForm registerClientForm = createRegisterClientForm(address, dateofBirth, district, birthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId, preferredName, region, registrationMode, sex, subDistrict, phoneNumber, patientType, staffId);
        registerClientForm.setDate(registartionDate);
        registerClientForm.setBcgDate(lastBCGDate);
        registerClientForm.setLastVitaminADate(lastVitADate);
        registerClientForm.setMeaslesDate(lastMeaslesDate);
        registerClientForm.setYellowFeverDate(lastYfDate);
        registerClientForm.setLastPentaDate(lastPentaDate);
        registerClientForm.setLastPenta(lastPenta);
        registerClientForm.setLastOPVDate(lastOPVDate);
        registerClientForm.setLastOPV(lastOPV);
        registerClientForm.setLastIPTiDate(lastIPTiDate);
        registerClientForm.setLastIPTi(lastIPTi);
        return registerClientForm;
    }

    private RegisterClientForm createRegisterClientFormForANCEnrollment(String address, Date dateofBirth, String district,
                                                                        Boolean birthDateEstimated, String motechFacilityId, String firstName, Boolean insured, String lastName, String middleName, String motechId, Date nhisExpDate, String nhisNumber, String parentId, String preferredName, String region, RegistrationType registrationMode, String sex, String subDistrict, String phoneNumber, PatientType patientType, Date expDeliveryDate, Boolean deliveryDateConfirmed, Double height, Integer gravida, Integer parity, Date lastIPTDate, Date lastTTDate, String lastIPT, String lastTT, String staffId) {
        RegisterClientForm registerClientForm = createRegisterClientForm(address, dateofBirth, district, birthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId, preferredName, region, registrationMode, sex, subDistrict, phoneNumber, patientType, staffId);

        registerClientForm.setExpDeliveryDate(expDeliveryDate);
        registerClientForm.setDeliveryDateConfirmed(deliveryDateConfirmed);
        registerClientForm.setHeight(height);
        registerClientForm.setGravida(gravida);
        registerClientForm.setParity(parity);
        registerClientForm.setLastIPTDate(lastIPTDate);
        registerClientForm.setLastTTDate(lastTTDate);
        registerClientForm.setLastIPT(lastIPT);
        registerClientForm.setLastTT(lastTT);

        return registerClientForm;
    }


    private RegisterClientForm createRegisterClientForm(String address, Date dateofBirth, String district, Boolean birthDateEstimated, String motechFacilityId, String firstName, Boolean insured, String lastName, String middleName, String motechId, Date nhisExpDate, String nhisNumber, String parentId, String preferredName, String region, RegistrationType registrationMode, String sex, String subDistrict, String phoneNumber, PatientType patientType, String staffId) {
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setStaffId(staffId);
        registerClientForm.setAddress(address);
        registerClientForm.setDateOfBirth(dateofBirth);
        registerClientForm.setDistrict(district);
        registerClientForm.setEstimatedBirthDate(birthDateEstimated);
        registerClientForm.setFacilityId(motechFacilityId);
        registerClientForm.setFirstName(firstName);
        registerClientForm.setInsured(insured);
        registerClientForm.setLastName(lastName);
        registerClientForm.setMiddleName(middleName);
        registerClientForm.setMotechId(motechId);
        registerClientForm.setNhisExpires(nhisExpDate);
        registerClientForm.setNhis(nhisNumber);
        registerClientForm.setMotherMotechId(parentId);
        registerClientForm.setPrefferedName(preferredName);
        registerClientForm.setRegion(region);
        registerClientForm.setRegistrationMode(registrationMode);
        registerClientForm.setSex(sex);
        registerClientForm.setSubDistrict(subDistrict);
        registerClientForm.setRegistrantType(patientType);
        registerClientForm.setPhoneNumber(phoneNumber);
        return registerClientForm;
    }


    private void assertRegisterPatient(String address, Date dateofBirth, Boolean birthDateEstimated, String facilityId, String firstName, Boolean insured, String lastName, String middleName, String motechId, Date nhisExpDate, String nhisNumber, String preferredName, String sex, String phoneNumber, Patient savedPatient, MRSPerson mrsPerson) {
        assertThat(mrsPerson.getAddress(), is(equalTo(address)));
        assertThat(mrsPerson.getDateOfBirth(), is(equalTo(dateofBirth)));
        assertThat(savedPatient.getMrsPatient().getFacility().getId(), is(equalTo(facilityId)));
        assertThat(savedPatient.getMrsPatient().getFacility().getCountyDistrict(), is(equalTo(null)));
        assertThat(savedPatient.getMrsPatient().getFacility().getRegion(), is(equalTo(null)));
        assertThat(savedPatient.getMrsPatient().getFacility().getStateProvince(), is(equalTo(null)));
        assertThat(mrsPerson.getBirthDateEstimated(), is(equalTo(birthDateEstimated)));
        assertThat(mrsPerson.getFirstName(), is(equalTo(firstName)));
        assertThat(mrsPerson.getLastName(), is(equalTo(lastName)));
        assertThat(mrsPerson.getMiddleName(), is(equalTo(middleName)));
        assertThat(savedPatient.getMrsPatient().getMotechId(), is(equalTo(motechId)));
        assertThat(mrsPerson.getPreferredName(), is(equalTo(preferredName)));
        assertThat(mrsPerson.getGender(), is(equalTo(sex)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute())))).value(), is(equalTo(nhisExpDate.toString())));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_NUMBER.getAttribute())))).value(), is(equalTo(nhisNumber)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.INSURED.getAttribute())))).value(), is(equalTo(insured.toString())));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.PHONE_NUMBER.getAttribute())))).value(), is(equalTo(phoneNumber)));
    }

    private void assertCWCRegistration(String motechFacilityId, String motechId, Date registartionDate, Date lastBCGDate, Date lastVitADate, Date lastMeaslesDate, Date lastYfDate, Date lastPentaDate, Date lastOPVDate, Date lastIPTiDate, String staffId, int lastPenta, int lastOPV, CwcVO cwcVO) {
        assertThat(motechFacilityId, is(cwcVO.getFacilityId()));
        assertThat(registartionDate, is(cwcVO.getRegistrationDate()));
        assertThat(motechId, is(cwcVO.getPatientMotechId()));
        assertThat(lastBCGDate, is(cwcVO.getBcgDate()));
        assertThat(lastVitADate, is(cwcVO.getVitADate()));
        assertThat(lastMeaslesDate, is(cwcVO.getMeaslesDate()));
        assertThat(lastYfDate, is(cwcVO.getYfDate()));
        assertThat(lastPentaDate, is(cwcVO.getLastPentaDate()));
        assertThat(lastPenta, is(cwcVO.getLastPenta()));
        assertThat(lastOPVDate, is(cwcVO.getLastOPVDate()));
        assertThat(lastOPV, is(cwcVO.getLastOPV()));
        assertThat(lastIPTiDate, is(cwcVO.getLastIPTiDate()));
        assertThat(staffId, is(cwcVO.getStaffId()));
    }

}
