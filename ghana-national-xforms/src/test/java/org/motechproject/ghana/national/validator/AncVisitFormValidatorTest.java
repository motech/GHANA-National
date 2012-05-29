package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ANCVisitForm;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.*;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class AncVisitFormValidatorTest {

    @Mock
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Mock
    private AllEncounters mockAllEncounters;

    private AncVisitFormValidator validator;

    @Before
    public void setUp() {
        initMocks(this);
        validator = new AncVisitFormValidator();
        setField(validator, "formValidator", formValidator);
        setField(validator, "allEncounters", mockAllEncounters);
    }

    @Test
    public void shouldValidateANCVisitForm() {
        ANCVisitForm formBean = new ANCVisitForm();
        String motechId = "motechId";
        formBean.setMotechId(motechId);
        formBean.setStaffId("staffId");
        formBean.setFacilityId("facilityId");
        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().dead(false), new MRSFacility("facilityId")));
        patient.getMrsPatient().getPerson().gender("F");
        FormBeanGroup formBeanGroup = new FormBeanGroup(Collections.<FormBean>emptyList());

        //patient exists in db,registered for ANC
        MRSEncounter encounter = mock(MRSEncounter.class);
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        when(mockAllEncounters.getLatest(motechId,ANC_REG_VISIT.value())).thenReturn(encounter);
        List<FormError> errors = validator.validate(formBean, formBeanGroup, Arrays.<FormBean>asList(formBean));

        verify(formValidator).validateIfStaffExists(formBean.getStaffId());
        verify(formValidator).validateIfFacilityExists(formBean.getFacilityId());
        assertRegANCDependencyHasNoError(errors);

        //patient is not a female
        patient.getMrsPatient().getPerson().gender("M");
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        errors = validator.validate(formBean,formBeanGroup, Arrays.<FormBean>asList(formBean));
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG)));

        //patient not exists in db,no anc form submitted
        patient.getMrsPatient().getPerson().gender("F");
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        when(mockAllEncounters.getLatest(motechId, ANC_REG_VISIT.value())).thenReturn(null);
        errors = validator.validate(formBean,formBeanGroup, Arrays.<FormBean>asList(formBean));
        assertThat(errors,hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "not registered for ANC")));

        //patient exists in db,reg anc form submitted
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        RegisterANCForm registerANCForm = new RegisterANCForm();
        registerANCForm.setFormname("registerANC");
        registerANCForm.setMotechId(motechId);
        formBeanGroup=new FormBeanGroup(Arrays.<FormBean>asList(formBean, registerANCForm));
        errors = validator.validate(formBean,formBeanGroup, Arrays.<FormBean>asList(formBean, registerANCForm));
        assertRegANCDependencyHasNoError(errors);

        //patient not in db,reg client form submitted with ANC
        when(formValidator.getPatient(motechId)).thenReturn(null);
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setMotechId(motechId);
        registerClientForm.setRegistrantType(PatientType.PREGNANT_MOTHER);
        formBeanGroup=new FormBeanGroup(Arrays.<FormBean>asList(formBean, registerClientForm));
        errors = validator.validate(formBean,formBeanGroup, Arrays.<FormBean>asList(formBean, registerClientForm));
        assertRegANCDependencyHasNoError(errors);
    }

    private void assertRegANCDependencyHasNoError(List<FormError> errors) {
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND))));
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, IS_NOT_ALIVE))));
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, GENDER_ERROR_MSG))));
        assertThat(errors, not(hasItem(new FormError("Sex", GENDER_ERROR_MSG))));
        assertThat(errors,not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "not registered for ANC"))));
        assertThat(errors,not(hasItem(new FormError("Client type", "should be a pregnant mother"))));
    }

}
