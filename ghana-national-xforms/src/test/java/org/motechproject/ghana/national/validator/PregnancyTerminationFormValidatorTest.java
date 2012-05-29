package org.motechproject.ghana.national.validator;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;

public class PregnancyTerminationFormValidatorTest {
    private PregnancyTerminationFormValidator pregnancyTerminationFormValidator;
    @Mock
    private FormValidator formValidator;

    @Mock
    private AllEncounters allEncounters;


    @Before
    public void setUp() {
        initMocks(this);
        pregnancyTerminationFormValidator = spy(new PregnancyTerminationFormValidator());
        ReflectionTestUtils.setField(pregnancyTerminationFormValidator, "formValidator", formValidator);
        ReflectionTestUtils.setField(pregnancyTerminationFormValidator, "allEncounters", allEncounters);
    }

    @Test
    public void shouldValidateIfThePatientHasValidANCRegistration() throws Exception {
        String facilityId = "facilityId";
        String motechId = "motechId";
        String staffId = "staffId";
        PregnancyTerminationForm formBean = setUpForm(facilityId, motechId, staffId);
        when(formValidator.validateIfFacilityExists(facilityId)).thenReturn(Arrays.asList(new FormError("facility", "not found")));
        when(formValidator.validateIfStaffExists(staffId)).thenReturn(Arrays.asList(new FormError("staff", "not found")));

        // does not have valid ANC encounter
        when(allEncounters.getLatest(motechId, ANC_REG_VISIT.value())).thenReturn(null);

        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().dead(false), new MRSFacility(facilityId)));
        doReturn(patient).when(formValidator).getPatient(motechId);

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean);
        List<FormError> formErrors = pregnancyTerminationFormValidator.validate(formBean, new FormBeanGroup(formBeans), formBeans);

        assertThat(formErrors, hasItem(new FormError("facility", "not found")));
        assertThat(formErrors, hasItem(new FormError("staff", "not found")));

        assertThat(formErrors, hasItem(new FormError("motechId", "not registered for ANC")));

        // has valid ANC encounter
        final MRSEncounter encounter = mock(MRSEncounter.class);
        when(allEncounters.getLatest(motechId, ANC_REG_VISIT.value())).thenReturn(encounter);
        formBeans = Arrays.<FormBean>asList(formBean);
        formErrors = pregnancyTerminationFormValidator.validate(formBean, new FormBeanGroup(formBeans), formBeans);
        assertThat(formErrors, not(hasItem(new FormError("motechId", "not registered for ANC"))));

        // has Reg ANC form
        final RegisterANCForm ancForm = new RegisterANCForm();
        ancForm.setFormname("registerANC");
        formBeans = Arrays.<FormBean>asList(ancForm);
        formErrors = pregnancyTerminationFormValidator.validate(formBean, new FormBeanGroup(formBeans), formBeans);
        MatcherAssert.assertThat(formErrors, not(hasItem(new FormError("motechId", "not registered for ANC"))));
        MatcherAssert.assertThat(formErrors, not(hasItem(new FormError("motechId", "not found"))));


        // does not have ANC reg form
        doReturn(null).when(formValidator).getPatient(motechId);
        formBeans = Arrays.<FormBean>asList(formBean);
        formErrors = pregnancyTerminationFormValidator.validate(formBean, new FormBeanGroup(formBeans), formBeans);
        MatcherAssert.assertThat(formErrors, hasItem(new FormError("motechId", "not registered for ANC")));

    }

    private PregnancyTerminationForm setUpForm(String facilityId, String motechId, String staffId) {
        PregnancyTerminationForm formBean = new PregnancyTerminationForm();
        formBean.setFacilityId(facilityId);
        formBean.setStaffId(staffId);
        formBean.setMotechId(motechId);
        return formBean;
    }
}
