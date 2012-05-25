package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CWCVisitForm;
import org.motechproject.ghana.national.bean.RegisterCWCForm;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.*;
import static org.motechproject.ghana.national.domain.EncounterType.CWC_REG_VISIT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class CwcVisitFormValidatorTest {

    @Mock
    private FormValidator formValidator;

    @Mock
    private AllEncounters mockAllEncounters;

    private CwcVisitFormValidator validator;

    @Before
    public void setUp() {
        initMocks(this);
        validator = new CwcVisitFormValidator();
        setField(validator, "formValidator", formValidator);
        setField(validator, "allEncounters", mockAllEncounters);
    }

    @Test
    public void shouldValidateCWCVisitForm() {
        CWCVisitForm formBean = new CWCVisitForm();
        String motechId = "1231231";
        formBean.setMotechId(motechId);
        formBean.setStaffId("123");
        formBean.setFacilityId("1234");

        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().dead(false), new MRSFacility("facilityId")));
        patient.getMrsPatient().getPerson().age(3);
        FormBeanGroup formBeanGroup = new FormBeanGroup(Collections.<FormBean>emptyList());

        //patient exists in db,registered for CWC
        MRSEncounter encounter = mock(MRSEncounter.class);
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        when(mockAllEncounters.getLatest(motechId, CWC_REG_VISIT.value())).thenReturn(encounter);
        List<FormError> errors = validator.validate(formBean, formBeanGroup);
        assertRegCWCDependencyHasNoError(errors);

        //patient exists in db,incorrect age
        patient.getMrsPatient().getPerson().age(6);
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        errors = validator.validate(formBean, formBeanGroup);
        assertThat(errors, hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)));

        //patient exists in db,no cwc form submitted
        patient.getMrsPatient().getPerson().age(4);
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        when(mockAllEncounters.getLatest(motechId, CWC_REG_VISIT.value())).thenReturn(null);
        errors = validator.validate(formBean, formBeanGroup);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "not registered for CWC")));

        //patient not in db,no form submitted
        when(formValidator.getPatient(motechId)).thenReturn(null);
        when(mockAllEncounters.getLatest(motechId, CWC_REG_VISIT.value())).thenReturn(null);
        errors = validator.validate(formBean, formBeanGroup);
        assertThat(errors, hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, NOT_FOUND)));

        //patient exists in db,reg cwc form submitted
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        RegisterCWCForm registerCWCForm = new RegisterCWCForm();
        registerCWCForm.setFormname("registerCWC");
        registerCWCForm.setMotechId(motechId);
        formBeanGroup = new FormBeanGroup(Arrays.<FormBean>asList(registerCWCForm));
        errors = validator.validate(formBean, formBeanGroup);
        assertRegCWCDependencyHasNoError(errors);

        //patient not in db,reg client form submitted with CWC
        when(formValidator.getPatient(motechId)).thenReturn(null);
        RegisterClientForm registerClientForm = new RegisterClientForm();
        registerClientForm.setFormname("registerPatient");
        registerClientForm.setMotechId(motechId);
        registerClientForm.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
        formBeanGroup=new FormBeanGroup(Arrays.<FormBean>asList(registerClientForm));
        errors = validator.validate(formBean,formBeanGroup);
        assertRegCWCDependencyHasNoError(errors);
    }

    private void assertRegCWCDependencyHasNoError(List<FormError> errors) {
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME,NOT_FOUND))));
        assertThat(errors,not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME,IS_NOT_ALIVE))));
        assertThat(errors,not(hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG))));
        assertThat(errors, not(hasItem(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "not registered for CWC"))));
        assertThat(errors,not(hasItem(new FormError("Client type", "should be a child"))));
    }
}
