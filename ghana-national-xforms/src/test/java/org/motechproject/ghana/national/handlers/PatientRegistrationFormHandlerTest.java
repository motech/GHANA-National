package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.LearnedFrom;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.ghana.national.domain.mobilemidwife.ReasonToJoin;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.util.AssertionUtility;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.FIRST_NAME;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.LAST_NAME;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.MOTECH_ID;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.REGISTER_SUCCESS_SMS_KEY;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;

public class PatientRegistrationFormHandlerTest {

    @Mock
    PatientService mockPatientService;
    @Mock
    FacilityService mockFacilityService;
    @Mock
    CareService mockCareService;
    @Mock
    MobileMidwifeService mockMobileMidwifeService;
    @Mock
    SMSGateway mockSMSGateway;

    @InjectMocks
    PatientRegistrationFormHandler patientRegistrationFormHandler = new PatientRegistrationFormHandler();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldRethrowException() throws PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        doThrow(new RuntimeException()).when(mockPatientService).registerPatient(Matchers.<Patient>any(), anyString(), Matchers.<Date>any());
        try {
            patientRegistrationFormHandler.handleFormEvent(new RegisterClientForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("Encountered exception while processing patient reg form"));
        }
    }

    @Test
    public void shouldHandleRegisterClientEventAndInvokeService() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        String address = "Address";
        Date dateofBirth = new Date(10, 10, 2011);
        String district = "District";
        Boolean isBirthDateEstimated = true;
        String motechFacilityId = "MotechFacilityID";
        String facilityId = "Facility Id";
        final String firstName = "FirstName";
        Boolean insured = true;
        final String lastName = "LastName";
        String middleName = "MiddleName";
        final String motechId = "motechId";
        Date nhisExpDate = new Date(10, 10, 2022);
        String nhisNumber = "NhisNumber";
        String parentId = "ParentId";
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        String phoneNumber = "0123456789";
        final String staffId = "456";
        PatientType patientType = PatientType.CHILD_UNDER_FIVE;

        RegisterClientForm registerClientForm = createRegisterClientForm(address, dateofBirth, district, isBirthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId, region, registrationMode, sex, subDistrict, phoneNumber, patientType, staffId);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        ArgumentCaptor<String> staffIdCaptor = ArgumentCaptor.forClass(String.class);
        Patient savedPatient = new Patient(new MRSPatient(motechId, new MRSPerson().firstName(firstName).lastName(lastName).dateOfBirth(DateUtil.newDate(2000, 1, 1).toDate()), null));
        doReturn(savedPatient).when(mockPatientService).registerPatient(patientArgumentCaptor.capture(), staffIdCaptor.capture(), eq(registerClientForm.getDate()));

        Facility facility = mock(Facility.class);
        when(facility.mrsFacilityId()).thenReturn(facilityId);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);

        patientRegistrationFormHandler.handleFormEvent(registerClientForm);

        Patient patientPassedToRegisterService = patientArgumentCaptor.getValue();
        MRSPerson mrsPerson = patientPassedToRegisterService.getMrsPatient().getPerson();
        assertRegisterPatient(address, dateofBirth, isBirthDateEstimated, facilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, sex, phoneNumber, patientPassedToRegisterService, mrsPerson, parentId);

        ArgumentCaptor<Map> smsTemplateArgCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockSMSGateway).dispatchSMS(eq(REGISTER_SUCCESS_SMS_KEY), smsTemplateArgCaptor.capture(), eq(registerClientForm.getSender()));
        AssertionUtility.assertContainsTemplateValues(new HashMap<String, String>() {{
            put(MOTECH_ID, motechId);
            put(FIRST_NAME, firstName);
            put(LAST_NAME, lastName);
        }}, smsTemplateArgCaptor.getValue());
    }


    @Test
    public void shouldRunAsAdminUser() throws NoSuchMethodException {
        assertThat(patientRegistrationFormHandler.getClass().getMethod("handleFormEvent", new Class[]{RegisterClientForm.class}).getAnnotation(LoginAsAdmin.class), is(not(equalTo(null))));
    }

    @Test
    public void shouldNotRegisterForMobileMidwifeIfConsentIsNotGiven() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
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
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        String phoneNumber = "0123123123";
        PatientType patientType = PatientType.CHILD_UNDER_FIVE;

        ServiceType serviceType = ServiceType.PREGNANCY;
        ReasonToJoin reasonToJoin = ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH;
        Medium medium = Medium.SMS;
        DayOfWeek dayOfWeek = DayOfWeek.Monday;
        Time timeOfDay = new Time(10, 30);
        Language language = Language.EN;
        LearnedFrom learnedFrom = LearnedFrom.FRIEND;
        String mmRegPhone = "0123456789";
        PhoneOwnership phoneOwnership = PhoneOwnership.HOUSEHOLD;
        Boolean consent = Boolean.FALSE;
        boolean enroll = false;
        String staffId = "123";
        RegisterClientForm registerClientForm = createRegisterClientForm(address, dateofBirth, district, isBirthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId,
                region, registrationMode, sex, subDistrict, phoneNumber, patientType, staffId);

        addMobileMidwifeRegistrationDetails(registerClientForm, serviceType, reasonToJoin, medium, dayOfWeek, timeOfDay, language, learnedFrom, mmRegPhone, phoneOwnership, consent, enroll);


        Facility facility = mock(Facility.class);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);
        when(facility.mrsFacilityId()).thenReturn(facilityId);

        when(mockPatientService.registerPatient(any(Patient.class), any(String.class), Matchers.<Date>any())).thenReturn(new Patient(new MRSPatient(motechId, new MRSPerson().dateOfBirth(new Date()), null)));

        patientRegistrationFormHandler.handleFormEvent(registerClientForm);
        verify(mockMobileMidwifeService, never()).register(Matchers.<MobileMidwifeEnrollment>any());
    }

    @Test
    public void shouldRegisterForCWCAndMobileMidwife() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
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
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        String phoneNumber = "0123123123";
        PatientType patientType = PatientType.CHILD_UNDER_FIVE;

        final Date registartionDate = new Date(2011, 9, 1);
        final Date lastBCGDate = new Date(2011, 10, 1);
        final Date lastVitADate = new Date(2011, 11, 1);
        final Date lastMeaslesDate = new Date(2011, 9, 2);
        final Date lastYfDate = new Date(2011, 11, 3);
        final Date lastPentaDate = new Date(2011, 11, 4);
        final Date lastOPVDate = new Date(2011, 11, 5);
        final Date lastIPTiDate = new Date(2011, 11, 6);
        final Date lastRotavirusDate = new Date(2011, 11, 6);
        final Date lastPneumococcalDate = new Date(2011, 11, 3);
        final String staffId = "456";
        final int lastPenta = 1;
        final int lastOPV = 0;
        final int lastIPTi = 1;
        final int lastRotavirus = 1;
        final int lastPneumo = 1;
        String lastVitA = "blue";

        ServiceType serviceType = ServiceType.PREGNANCY;
        ReasonToJoin reasonToJoin = ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH;
        Medium medium = Medium.SMS;
        DayOfWeek dayOfWeek = DayOfWeek.Monday;
        Time timeOfDay = new Time(10, 30);
        Language language = Language.EN;
        LearnedFrom learnedFrom = LearnedFrom.FRIEND;
        String mmRegPhone = "0123456789";
        PhoneOwnership phoneOwnership = PhoneOwnership.HOUSEHOLD;
        Boolean consent = Boolean.TRUE;
        boolean enroll = true;

        Integer lastMeasles = 1;
        RegisterClientForm registerClientForm = createRegisterClientFormForCWCEnrollment(address, dateofBirth, district, isBirthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId,
                region, registrationMode, sex, subDistrict, phoneNumber, patientType, registartionDate,
                lastBCGDate, lastVitADate, lastMeaslesDate, lastYfDate, lastPentaDate, lastOPVDate, lastIPTiDate,
                staffId, lastVitA, lastPenta, lastOPV, lastIPTi, lastRotavirus, lastRotavirusDate, lastPneumo, lastPneumococcalDate, lastMeasles);

        addMobileMidwifeRegistrationDetails(registerClientForm, serviceType, reasonToJoin, medium, dayOfWeek, timeOfDay, language, learnedFrom, mmRegPhone, phoneOwnership, consent, enroll);

        Facility facility = mock(Facility.class);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);
        when(facility.mrsFacilityId()).thenReturn(facilityId);

        when(mockPatientService.registerPatient(any(Patient.class), any(String.class), Matchers.<Date>any())).thenReturn(new Patient(new MRSPatient(motechId, new MRSPerson().dateOfBirth(new Date()), null)));
        doNothing().when(mockCareService).enroll(any(CwcVO.class));
        patientRegistrationFormHandler.handleFormEvent(registerClientForm);
        ArgumentCaptor<MobileMidwifeEnrollment> mobileMidwifeEnrollmentArgumentCaptor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        final ArgumentCaptor<CwcVO> cwcVOArgumentCaptor = ArgumentCaptor.forClass(CwcVO.class);
        verify(mockCareService).enroll(cwcVOArgumentCaptor.capture());
        verify(mockMobileMidwifeService).register(mobileMidwifeEnrollmentArgumentCaptor.capture());
        final CwcVO cwcVO = cwcVOArgumentCaptor.getValue();
        final MobileMidwifeEnrollment mobileMidwifeEnrollment = mobileMidwifeEnrollmentArgumentCaptor.getValue();

        assertCWCRegistration(motechFacilityId, facilityId, motechId, registartionDate, lastBCGDate, lastVitADate, lastMeaslesDate, lastMeasles, lastYfDate, lastPentaDate, lastOPVDate, lastIPTiDate, staffId, lastVitA, lastPenta, lastOPV, cwcVO, mobileMidwifeEnrollment);
        assertMobileMidwifeRegistration(mobileMidwifeEnrollment, staffId, motechFacilityId, motechId, serviceType, reasonToJoin, medium, dayOfWeek, timeOfDay, language, learnedFrom, mmRegPhone, phoneOwnership, consent, registartionDate);
    }

    @Test
    public void shouldRegisterForANCAndMobileMidwife() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException, ObservationNotFoundException {

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
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        String phoneNumber = "0123123123";
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

        //New ANC Observation

        String lastHbLevels = "lastHbLevels";
        String lastMotherVitaminA = "lastMotherVitaminA";
        String lastIronOrFolate = "lastIronOrFolate";
        String lastSyphilis = "lastSyphilis";
        String lastMalaria = "lastMalaria";
        String lastDiarrhea = "lastDiarrhea";
        String lastPnuemonia = "lastPnuemonia";
        Date lastHbDate = new Date(10, 12, 2000);
        Date lastMotherVitaminADate = new Date(10, 12, 2000);
        Date lastIronOrFolateDate = new Date(10, 12, 2000);
        Date lastSyphilisDate = new Date(10, 12, 2000);
        Date lastMalariaDate = new Date(10, 12, 2000);
        Date lastDiarrheaDate = new Date(10, 12, 2000);
        Date lastPnuemoniaDate = new Date(10, 12, 2000);

        ServiceType serviceType = ServiceType.PREGNANCY;
        ReasonToJoin reasonToJoin = ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH;
        Medium medium = Medium.SMS;
        DayOfWeek dayOfWeek = DayOfWeek.Monday;
        Time timeOfDay = new Time(10, 30);
        Language language = Language.EN;
        LearnedFrom learnedFrom = LearnedFrom.FRIEND;
        String mmRegPhone = "0123456789";
        PhoneOwnership phoneOwnership = PhoneOwnership.HOUSEHOLD;
        Boolean consent = Boolean.TRUE;
        boolean enroll = true;

        Date registrationDate = newDate(2010, 4, 4).toDate();
        RegisterClientForm registerClientForm = createRegisterClientFormForANCEnrollment(address, dateofBirth, district, isBirthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, null, nhisExpDate, nhisNumber, parentId,
                region, registrationMode, sex, subDistrict, phoneNumber, patientType, expDeliveryDate, deliveryDateConfirmed,
                height, gravida, parity, lastIPTDate, lastTTDate, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate,
                lastDiarrheaDate, lastPnuemoniaDate, lastIPT, lastTT, lastHbLevels, lastMotherVitaminA, lastIronOrFolate, lastSyphilis, lastMalaria, lastDiarrhea, lastPnuemonia, staffId, registrationDate);


        addMobileMidwifeRegistrationDetails(registerClientForm, serviceType, reasonToJoin, medium, dayOfWeek, timeOfDay,
                language, learnedFrom, mmRegPhone, phoneOwnership, consent, enroll);

        Facility facility = mock(Facility.class);
        doReturn(facility).when(mockFacilityService).getFacilityByMotechId(motechFacilityId);
        when(facility.mrsFacilityId()).thenReturn(facilityId);

        when(mockPatientService.registerPatient(any(Patient.class), any(String.class), Matchers.<Date>any()))
                .thenReturn(new Patient(new MRSPatient(motechId, new MRSPerson().dateOfBirth(new Date()), null)));

        doNothing().when(mockCareService).enroll(any(ANCVO.class));
        patientRegistrationFormHandler.handleFormEvent(registerClientForm);
        ArgumentCaptor<MobileMidwifeEnrollment> mobileMidwifeEnrollmentArgumentCaptor = ArgumentCaptor.forClass(MobileMidwifeEnrollment.class);
        final ArgumentCaptor<ANCVO> ancvoArgumentCaptor = ArgumentCaptor.forClass(ANCVO.class);
        verify(mockCareService).enroll(ancvoArgumentCaptor.capture());
        verify(mockMobileMidwifeService).register(mobileMidwifeEnrollmentArgumentCaptor.capture());

        final ANCVO ancVO = ancvoArgumentCaptor.getValue();
        final MobileMidwifeEnrollment mobileMidwifeEnrollment = mobileMidwifeEnrollmentArgumentCaptor.getValue();

        assertANCRegistration(facilityId, motechId, expDeliveryDate, deliveryDateConfirmed, height, gravida, parity,
                lastIPTDate, lastTTDate, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate,
                lastIPT, lastTT,lastHbLevels, lastMotherVitaminA, lastIronOrFolate, lastSyphilis,lastMalaria, lastDiarrhea, lastPnuemonia, staffId, ancVO);
        assertMobileMidwifeRegistration(mobileMidwifeEnrollment, staffId, motechFacilityId, motechId, serviceType,
                reasonToJoin, medium, dayOfWeek, timeOfDay, language, learnedFrom, mmRegPhone, phoneOwnership, consent, registerClientForm.getDate());
    }

    private RegisterClientForm createRegisterClientFormForCWCEnrollment(
            String address, Date dateofBirth, String district, Boolean birthDateEstimated, String motechFacilityId, String firstName,
            Boolean insured, String lastName, String middleName, String motechId, Date nhisExpDate, String nhisNumber, String parentId,
            String region, RegistrationType registrationMode, String sex, String subDistrict, String phoneNumber,
            PatientType patientType, Date registartionDate, Date lastBCGDate, Date lastVitADate, Date lastMeaslesDate, Date lastYfDate,
            Date lastPentaDate, Date lastOPVDate, Date lastIPTiDate, String staffId, String lastVitA, Integer lastPenta, Integer lastOPV, Integer lastIPTi,
            Integer lastRotavirus, Date lastRotavirusDate, Integer lastPneumo, Date lastPneumoDate, Integer lastMeasles) {
        RegisterClientForm registerClientForm = createRegisterClientForm(address, dateofBirth, district, birthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId, region,
                registrationMode, sex, subDistrict, phoneNumber, patientType, staffId);
        registerClientForm.setDate(registartionDate);
        registerClientForm.setBcgDate(lastBCGDate);
        registerClientForm.setLastVitaminA(lastVitA);
        registerClientForm.setLastMeasles(lastMeasles);
        registerClientForm.setLastVitaminADate(lastVitADate);
        registerClientForm.setMeaslesDate(lastMeaslesDate);
        registerClientForm.setYellowFeverDate(lastYfDate);
        registerClientForm.setLastPentaDate(lastPentaDate);
        registerClientForm.setLastPenta(lastPenta);
        registerClientForm.setLastOPVDate(lastOPVDate);
        registerClientForm.setLastOPV(lastOPV);
        registerClientForm.setLastIPTiDate(lastIPTiDate);
        registerClientForm.setLastIPTi(lastIPTi);
        registerClientForm.setLastRotavirus(lastRotavirus);
        registerClientForm.setLastRotavirusDate(lastRotavirusDate);
        registerClientForm.setLastPneumococcal(lastPneumo);
        registerClientForm.setLastPneumococcalDate(lastPneumoDate);
        return registerClientForm;
    }


    private RegisterClientForm createRegisterClientFormForANCEnrollment(String address, Date dateofBirth, String district, Boolean birthDateEstimated,
                                                                        String motechFacilityId, String firstName, Boolean insured, String lastName,
                                                                        String middleName, String motechId, Date nhisExpDate, String nhisNumber, String parentId,
                                                                        String region, RegistrationType registrationMode, String sex, String subDistrict,
                                                                        String phoneNumber, PatientType patientType, Date expDeliveryDate, Boolean deliveryDateConfirmed,
                                                                        Double height, Integer gravida, Integer parity, Date lastIPTDate, Date lastTTDate,  Date lastHbDate,
                                                                        Date lastMotherVitaADate, Date lastIronDate, Date lastSyphDate, Date lastMalariaDate, Date lastDiarheaDate, Date lastPneumoDate,
                                                                        String lastIPT, String lastTT, String lastHbLevels, String lastMotherVitaA, String lastIron, String lastSyphilis, String lastMalaria,
                                                                        String lastDiarhea, String lastPneumonia, String staffId, Date registrationDate) {
        RegisterClientForm registerClientForm = createRegisterClientForm(address, dateofBirth, district, birthDateEstimated,
                motechFacilityId, firstName, insured, lastName, middleName, motechId, nhisExpDate, nhisNumber, parentId, region, registrationMode, sex, subDistrict, phoneNumber, patientType, staffId);

        registerClientForm.setExpDeliveryDate(expDeliveryDate);
        registerClientForm.setDeliveryDateConfirmed(deliveryDateConfirmed);
        registerClientForm.setHeight(height);
        registerClientForm.setGravida(gravida);
        registerClientForm.setParity(parity);
        registerClientForm.setLastIPTDate(lastIPTDate);
        registerClientForm.setLastTTDate(lastTTDate);

        registerClientForm.setLastHbDate(lastHbDate);
        registerClientForm.setLastMotherVitaminADate(lastMotherVitaADate);
        registerClientForm.setLastIronOrFolateDate(lastIronDate);
        registerClientForm.setLastSyphilisDate(lastSyphDate);
        registerClientForm.setLastMalariaDate(lastMalariaDate);
        registerClientForm.setLastDiarrheaDate(lastDiarheaDate);
        registerClientForm.setLastPnuemoniaDate(lastPneumoDate);

        registerClientForm.setLastIPT(lastIPT);
        registerClientForm.setLastTT(lastTT);

        registerClientForm.setLastHbLevels(lastHbLevels);
        registerClientForm.setLastMotherVitaminA(lastMotherVitaA);
        registerClientForm.setLastIronOrFolate(lastIron);
        registerClientForm.setLastSyphilis(lastSyphilis);
        registerClientForm.setLastMalaria(lastMalaria);
        registerClientForm.setLastDiarrhea(lastDiarhea);
        registerClientForm.setLastPnuemonia(lastPneumonia);

        registerClientForm.setDate(registrationDate);

        return registerClientForm;
    }

    private RegisterClientForm createRegisterClientForm(String address, Date dateofBirth, String district, Boolean birthDateEstimated,
                                                        String motechFacilityId, String firstName, Boolean insured,
                                                        String lastName, String middleName, String motechId, Date nhisExpDate,
                                                        String nhisNumber, String parentId, String region, RegistrationType registrationMode,
                                                        String sex, String subDistrict, String phoneNumber, PatientType patientType, String staffId) {
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setSender("0987654321");
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
        registerClientForm.setRegion(region);
        registerClientForm.setRegistrationMode(registrationMode);
        registerClientForm.setSex(sex);
        registerClientForm.setSubDistrict(subDistrict);
        registerClientForm.setRegistrantType(patientType);
        registerClientForm.setPhoneNumber(phoneNumber);
        return registerClientForm;
    }


    private void assertRegisterPatient(String address, Date dateofBirth, Boolean birthDateEstimated, String facilityId,
                                       String firstName, Boolean insured, String lastName,
                                       String middleName, String motechId, Date nhisExpDate, String nhisNumber, String sex,
                                       String phoneNumber, Patient savedPatient, MRSPerson mrsPerson, String parentId) {
        assertThat(mrsPerson.getAddress(), is(equalTo(address)));
        assertThat(savedPatient.getParentId(), is(equalTo(parentId)));
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
        assertThat(mrsPerson.getGender(), is(equalTo(sex)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute())))).value(),
                is(equalTo(new SimpleDateFormat(Constants.PATTERN_YYYY_MM_DD).format(nhisExpDate))));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_NUMBER.getAttribute())))).value(), is(equalTo(nhisNumber)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.INSURED.getAttribute())))).value(), is(equalTo(insured.toString())));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.PHONE_NUMBER.getAttribute())))).value(), is(equalTo(phoneNumber)));
    }

    private void assertCWCRegistration(String motechFacilityId, String facilityId, String motechId, Date registartionDate,
                                       Date lastBCGDate, Date lastVitADate, Date lastMeaslesDate, Integer lastMeasles, Date lastYfDate,
                                       Date lastPentaDate, Date lastOPVDate, Date lastIPTiDate, String staffId, String lastVitA,
                                       int lastPenta, int lastOPV, CwcVO cwcVO, MobileMidwifeEnrollment mobileMidwifeEnrollment) {
        assertThat(facilityId, is(cwcVO.getFacilityId()));
        assertThat(registartionDate, is(cwcVO.getRegistrationDate()));
        assertThat(motechId, is(cwcVO.getPatientMotechId()));
        assertThat(lastBCGDate, is(cwcVO.getCWCCareHistoryVO().getBcgDate()));
        assertThat(lastVitA, is(cwcVO.getCWCCareHistoryVO().getLastVitA()));
        assertThat(lastMeasles, is(cwcVO.getCWCCareHistoryVO().getLastMeasles()));
        assertThat(lastVitADate, is(cwcVO.getCWCCareHistoryVO().getVitADate()));
        assertThat(lastMeaslesDate, is(cwcVO.getCWCCareHistoryVO().getMeaslesDate()));
        assertThat(lastYfDate, is(cwcVO.getCWCCareHistoryVO().getYfDate()));
        assertThat(lastPentaDate, is(cwcVO.getCWCCareHistoryVO().getLastPentaDate()));
        assertThat(lastPenta, is(cwcVO.getCWCCareHistoryVO().getLastPenta()));
        assertThat(lastOPVDate, is(cwcVO.getCWCCareHistoryVO().getLastOPVDate()));
        assertThat(lastOPV, is(cwcVO.getCWCCareHistoryVO().getLastOPV()));
        assertThat(lastIPTiDate, is(cwcVO.getCWCCareHistoryVO().getLastIPTiDate()));
        assertThat(staffId, is(cwcVO.getStaffId()));
        assertThat(staffId, is(mobileMidwifeEnrollment.getStaffId()));
        assertThat(motechFacilityId, is(mobileMidwifeEnrollment.getFacilityId()));
        assertThat(motechId, is(mobileMidwifeEnrollment.getPatientId()));
    }

    private void assertANCRegistration(String facilityId, String motechId, Date expDeliveryDate, Boolean deliveryDateConfirmed, Double height, Integer gravida, Integer parity, Date lastIPTDate, Date lastTTDate, Date lastHbDate, Date lastVitaADate, Date lastIronDate, Date lastSyphDate, Date lastMalariaDate, Date lastDiarheaDate, Date lastPneumoDate,
                                       String lastIPT, String lastTT, String lastHBlevel, String lastMotherVitaA, String lastIron, String lastSyphilis, String lastMalaria, String lastDiarhea, String lastPneumonia, String staffId, ANCVO ancVO) {
        assertThat(ancVO.getFacilityId(), is(facilityId));
        assertThat(ancVO.getPatientMotechId(), is(motechId));
        assertThat(ancVO.getEstimatedDateOfDelivery(), is(expDeliveryDate));
        assertThat(ancVO.getDeliveryDateConfirmed(), is(deliveryDateConfirmed));
        assertThat(ancVO.getHeight(), is(height));
        assertThat(ancVO.getGravida(), is(gravida));
        assertThat(ancVO.getParity(), is(parity));
        assertThat(ancVO.getAncCareHistoryVO().getLastIPTDate(), is(lastIPTDate));
        assertThat(ancVO.getAncCareHistoryVO().getLastTTDate(), is(lastTTDate));

        //added by Freduah
        assertThat(ancVO.getAncCareHistoryVO().getLastHbDate(), is(lastHbDate));
        assertThat(ancVO.getAncCareHistoryVO().getLastMotherVitaminADate(), is(lastVitaADate));
        assertThat(ancVO.getAncCareHistoryVO().getLastIronOrFolateDate(), is(lastIronDate));
        assertThat(ancVO.getAncCareHistoryVO().getLastSyphilisDate(), is(lastSyphDate));
        assertThat(ancVO.getAncCareHistoryVO().getLastMalariaDate(), is(lastMalariaDate));
        assertThat(ancVO.getAncCareHistoryVO().getLastDiarrheaDate(), is(lastDiarheaDate));
        assertThat(ancVO.getAncCareHistoryVO().getLastPnuemoniaDate(), is(lastPneumoDate));


        assertThat(ancVO.getAncCareHistoryVO().getLastIPT(), is(lastIPT));
        assertThat(ancVO.getAncCareHistoryVO().getLastTT(), is(lastTT));

        //added by Freduah
        assertThat(ancVO.getAncCareHistoryVO().getLastHbLevels(), is(lastHBlevel));
        assertThat(ancVO.getAncCareHistoryVO().getLastMotherVitaminA(), is(lastMotherVitaA));
        assertThat(ancVO.getAncCareHistoryVO().getLastIronOrFolate(), is(lastIron));
        assertThat(ancVO.getAncCareHistoryVO().getLastSyphilis(), is(lastSyphilis));
        assertThat(ancVO.getAncCareHistoryVO().getLastMalaria(), is(lastMalaria));
        assertThat(ancVO.getAncCareHistoryVO().getLastDiarrhea(), is(lastDiarhea));
        assertThat(ancVO.getAncCareHistoryVO().getLastPnuemonia(), is(lastPneumonia));
    }

    private void addMobileMidwifeRegistrationDetails(RegisterClientForm registerClientForm, ServiceType serviceType, ReasonToJoin reasonToJoin, Medium medium, DayOfWeek dayOfWeek, Time timeOfDay, Language language, LearnedFrom learnedFrom, String phoneNumber, PhoneOwnership phoneOwnership, Boolean consent, boolean enroll) {
        registerClientForm.setEnroll(enroll);
        registerClientForm.setServiceType(serviceType);
        registerClientForm.setReasonToJoin(reasonToJoin);
        registerClientForm.setFormat(medium.getValue());
        registerClientForm.setDayOfWeek(dayOfWeek);
        registerClientForm.setTimeOfDay(timeOfDay);
        registerClientForm.setLanguage(language);
        registerClientForm.setLearnedFrom(learnedFrom);
        registerClientForm.setMmRegPhone(phoneNumber);
        registerClientForm.setPhoneOwnership(phoneOwnership);
        registerClientForm.setConsent(consent);
    }

    private void assertMobileMidwifeRegistration(MobileMidwifeEnrollment mobileMidwifeEnrollment, String staffId, String motechFacilityId,
                                                 String motechId, ServiceType serviceType, ReasonToJoin reasonToJoin, Medium medium, DayOfWeek dayOfWeek,
                                                 Time timeOfDay, Language language, LearnedFrom learnedFrom, String phoneNumber, PhoneOwnership phoneOwnership,
                                                 Boolean consent, Date registrationDateTime) {
        assertThat(mobileMidwifeEnrollment.getStaffId(), is(staffId));
        assertThat(mobileMidwifeEnrollment.getFacilityId(), is(motechFacilityId));
        assertThat(mobileMidwifeEnrollment.getPatientId(), is(motechId));
        assertThat(mobileMidwifeEnrollment.getServiceType(), is(serviceType));
        assertThat(mobileMidwifeEnrollment.getReasonToJoin(), is(reasonToJoin));
        assertThat(mobileMidwifeEnrollment.getMedium(), is(medium));
        assertThat(mobileMidwifeEnrollment.getDayOfWeek(), is(dayOfWeek));
        assertThat(mobileMidwifeEnrollment.getTimeOfDay(), is(timeOfDay));
        assertThat(mobileMidwifeEnrollment.getLanguage(), is(language));
        assertThat(mobileMidwifeEnrollment.getLearnedFrom(), is(learnedFrom));
        assertThat(mobileMidwifeEnrollment.getPhoneNumber(), is(phoneNumber));
        assertThat(mobileMidwifeEnrollment.getPhoneOwnership(), is(phoneOwnership));
        assertThat(mobileMidwifeEnrollment.getConsent(), is(consent));
        assertThat(mobileMidwifeEnrollment.getEnrollmentDateTime(), is(newDateTime(registrationDateTime)));
    }

}
