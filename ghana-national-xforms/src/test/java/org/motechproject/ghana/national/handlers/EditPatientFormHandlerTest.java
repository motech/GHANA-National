package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.EditClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.*;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static ch.lambdaj.Lambda.*;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EditPatientFormHandlerTest {

    @Mock
    PatientService mockPatientService;
    @Mock
    FacilityService mockFacilityService;
    EditPatientFormHandler editPatientFormHandler;
    private EditClientForm editClientForm = new EditClientForm();


    @Before
    public void setUp() {
        initMocks(this);
        editPatientFormHandler = new EditPatientFormHandler();
        ReflectionTestUtils.setField(editPatientFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(editPatientFormHandler, "facilityService", mockFacilityService);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(mockPatientService).updatePatient(Matchers.<Patient>any(), anyString(), Matchers.<Date>any());
        try {
            editPatientFormHandler.handleFormEvent(new EditClientForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("Encountered exception processing edit patient form"));
        }
    }

    @Test
    public void shouldCreateAnUpdatedPatientObject() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String facilityId = "1";
        String updatedFacilityId = "2";
        String staffId = "654";
        MRSFacility mrsFacility = new MRSFacility(facilityId);
        Facility facility = new Facility(mrsFacility).mrsFacilityId(facilityId);
        MRSFacility mrsFacilityWherePatientWasEdited = new MRSFacility(updatedFacilityId);
        Facility facilityWherePatientWasEdited = new Facility(mrsFacilityWherePatientWasEdited);
        facilityWherePatientWasEdited.mrsFacilityId(updatedFacilityId);
        String first = "first";
        String middle = "middle";
        String last = "last";
        Date dateOfBirth = new Date();
        String gender = "male";
        String address = "address";
        String patientId = "1000";
        Boolean birthDateEstimated = true;
        Date nhisExpDate = new Date();
        String nhisNumber = "123";
        String parentId = "123";
        String sex = "M";
        String phoneNumber = "0123456789";
        String facilityIdWherePatientWasEdited = "999";
        Date updatedDate = new Date();

        populateFormWithValues(facilityId, first, middle, last, dateOfBirth, address, patientId, nhisExpDate, nhisNumber,
                parentId, sex, phoneNumber, facilityIdWherePatientWasEdited, staffId, updatedDate);

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last)
                .dateOfBirth(dateOfBirth).birthDateEstimated(birthDateEstimated).gender(gender).address(address);
        mrsPerson.addAttribute(new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.toString(), nhisExpDate.toString()));
        mrsPerson.addAttribute(new Attribute(PatientAttributes.NHIS_NUMBER.toString(), nhisNumber));
        mrsPerson.addAttribute(new Attribute(PatientAttributes.PHONE_NUMBER.toString(), phoneNumber));
        final MRSPatient mrsPatient = new MRSPatient(patientId, mrsPerson, mrsFacility);
        final Patient patient = new Patient(mrsPatient);

        when(mockPatientService.getPatientByMotechId(patientId)).thenReturn(patient);
        when(mockFacilityService.getFacilityByMotechId(facilityId)).thenReturn(facility);
        when(mockFacilityService.getFacilityByMotechId(facilityIdWherePatientWasEdited)).thenReturn(facilityWherePatientWasEdited);
        MRSUser mrsUser = new MRSUser();
        mrsUser.person(mrsPerson);

        parameters.put(Constants.FORM_BEAN, editClientForm);
        MotechEvent event = new MotechEvent("subject", parameters);

        assertResult(facilityId, first, middle, last, dateOfBirth, address, patientId, nhisNumber, sex, phoneNumber, updatedDate, event);
    }

    private void populateFormWithValues(String facilityId, String first, String middle, String last, Date dateOfBirth,
                                        String address, String patientId, Date nhisExpDate, String nhisNumber,
                                        String parentId, String sex, String phoneNumber, String facilityIdWherePatientWasEdited,
                                        String staffId, Date updatedDate) {
        editClientForm.setMotechId(patientId);
        editClientForm.setStaffId(staffId);
        editClientForm.setAddress(address);
        editClientForm.setDateOfBirth(dateOfBirth);
        editClientForm.setFacilityId(facilityId);
        editClientForm.setFirstName(first);
        editClientForm.setMiddleName(middle);
        editClientForm.setLastName(last);
        editClientForm.setNhisExpires(nhisExpDate);
        editClientForm.setNhis(nhisNumber);
        editClientForm.setMotherMotechId(parentId);
        editClientForm.setSex(sex);
        editClientForm.setPhoneNumber(phoneNumber);
        editClientForm.setUpdatePatientFacilityId(facilityIdWherePatientWasEdited);
        editClientForm.setAddress(address);
        editClientForm.setDate(updatedDate);
    }

    private void assertResult(String facilityId, String first, String middle, String last,
                              Date dateOfBirth, String address,
                              String patientId, String nhisNumber, String sex, String phoneNumber, Date updatedDate, MotechEvent event) throws ParentNotFoundException {
        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        ArgumentCaptor<String> staffIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        doReturn(patientId).when(mockPatientService).updatePatient(patientArgumentCaptor.capture(), staffIdCaptor.capture(), dateArgumentCaptor.capture());

        editPatientFormHandler.handleFormEvent(editClientForm);

        Patient savedPatient = patientArgumentCaptor.getValue();
        MRSPerson savedPerson = savedPatient.getMrsPatient().getPerson();
        assertThat(savedPerson.getAddress(), is(equalTo(address)));
        assertThat(savedPerson.getDateOfBirth(), is(equalTo(dateOfBirth)));
        assertThat(savedPatient.getMrsPatient().getFacility().getId(), is(equalTo(facilityId)));
        assertThat(savedPerson.getFirstName(), is(equalTo(first)));
        assertThat(savedPerson.getLastName(), is(equalTo(last)));
        assertThat(savedPerson.getMiddleName(), is(equalTo(middle)));
        assertThat(savedPerson.getAddress(), is(equalTo(address)));
        assertThat(savedPatient.getMrsPatient().getMotechId(), is(equalTo(patientId)));
        assertThat(savedPerson.getGender(), is(equalTo(sex)));
        assertThat(dateArgumentCaptor.getValue(), is(updatedDate));

        assertThat(((Attribute) selectUnique(savedPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_NUMBER.getAttribute())))).value(), is(equalTo(nhisNumber)));
        assertThat(((Attribute) selectUnique(savedPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.PHONE_NUMBER.getAttribute())))).value(), is(equalTo(phoneNumber)));

        assertThat(savedPatient.getParentId(), is(equalTo(editClientForm.getMotherMotechId())));
    }

    @Test
    public void shouldRunAsAdminUser() throws NoSuchMethodException {
        assertThat(editPatientFormHandler.getClass().getMethod("handleFormEvent", new Class[]{EditClientForm.class}).getAnnotation(LoginAsAdmin.class), is(not(equalTo(null))));
    }
}
