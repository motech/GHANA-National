package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.EditClientForm;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.validator.patient.ExistsInDb;
import org.motechproject.ghana.national.validator.patient.IsAlive;
import org.motechproject.ghana.national.validator.patient.PatientValidator;
import org.motechproject.ghana.national.validator.patient.RegClientFormSubmittedInSameUpload;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.IS_NOT_ALIVE;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class EditClientFormValidatorTest {
    private EditClientFormValidator editClientFormValidator;

    @Mock
    private FormValidator formValidator;

    @Before
    public void setUp() {
        initMocks(this);
        editClientFormValidator = spy(new EditClientFormValidator());
        ReflectionTestUtils.setField(editClientFormValidator, "formValidator", formValidator);
    }

    @Test
    public void shouldVerifyEditClientForm() {
        EditClientForm editClientForm = new EditClientForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        editClientForm.setMotechId(motechId);
        editClientForm.setFacilityId(facilityId);
        editClientForm.setStaffId(staffId);

        final DependentValidator mockDependentValidator = mock(DependentValidator.class);
        when(editClientFormValidator.dependentValidator()).thenReturn(mockDependentValidator);

        PatientValidator expectedValidator = new ExistsInDb().onSuccess(new IsAlive()).onFailure(new RegClientFormSubmittedInSameUpload());

        Patient patient = mock(Patient.class);
        when(formValidator.getPatient(motechId)).thenReturn(patient);

        List<FormBean> formBeans = Arrays.<FormBean>asList(editClientForm);
        final FormBeanGroup formBeanGroup = new FormBeanGroup(formBeans);
        editClientFormValidator.validate(editClientForm, formBeanGroup, formBeans);

        verify(formValidator).validateIfStaffExists(staffId);
        verify(formValidator).validateIfFacilityExists(facilityId);
        verify(mockDependentValidator).validate(patient, formBeans, formBeans, expectedValidator);
    }

    @Test
    public void shouldValidateMothersMotechIdIfOneWasProvided() {
        EditClientForm editClientForm = new EditClientForm();

        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        String mothersMotechId = "mothersMotechId";
        editClientForm.setMotechId(motechId);
        editClientForm.setFacilityId(facilityId);
        editClientForm.setStaffId(staffId);
        editClientForm.setMotherMotechId(mothersMotechId);


        // mother not in db
        when(formValidator.getPatient(motechId)).thenReturn(null);
        List<FormBean> formBeans = Arrays.<FormBean>asList(editClientForm);
        List<FormError> errors = editClientFormValidator.validate(editClientForm, new FormBeanGroup(formBeans), formBeans);

        verify(formValidator).validateIfStaffExists(staffId);
        verify(formValidator).validateIfFacilityExists(facilityId);

        assertThat(errors, hasItem(new FormError("Mothers motech Id", NOT_FOUND)));

        // mother in db by dead
        Patient mother = new Patient(new MRSPatient(motechId, new MRSPerson().dead(true), new MRSFacility(facilityId)));
        when(formValidator.getPatient(mothersMotechId)).thenReturn(mother);
        formBeans = Arrays.<FormBean>asList(editClientForm);
        errors = editClientFormValidator.validate(editClientForm, new FormBeanGroup(formBeans), formBeans);

        assertThat(errors, hasItem(new FormError("Mothers motech Id", IS_NOT_ALIVE)));

        // mother in db and alive
        mother.getMrsPatient().getPerson().dead(false);
        formBeans = Arrays.<FormBean>asList(new RegisterClientForm());
        errors = editClientFormValidator.validate(editClientForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(errors, not(hasItem(new FormError("Mothers motech Id", NOT_FOUND))));
        assertThat(errors, not(hasItem(new FormError("Mothers motech Id", IS_NOT_ALIVE))));

        // mother's motech id not provided
        editClientForm.setMotherMotechId(null);
        when(formValidator.getPatient(motechId)).thenReturn(null);
        formBeans = Arrays.<FormBean>asList(editClientForm);
        errors = editClientFormValidator.validate(editClientForm, new FormBeanGroup(formBeans), formBeans);

        assertThat(errors, not(hasItem(new FormError("Mothers motech Id", NOT_FOUND))));
    }
}
