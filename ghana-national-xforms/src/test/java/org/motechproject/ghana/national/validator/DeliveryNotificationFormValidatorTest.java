package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.validator.patient.*;
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
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeliveryNotificationFormValidatorTest {

    DeliveryNotificationFormValidator validator;

    @Mock
    FormValidator formValidator;
    @Mock
    private AllEncounters allEncounters;

    @Before
    public void setUp() {
        initMocks(this);
        validator = spy(new DeliveryNotificationFormValidator());
        ReflectionTestUtils.setField(validator, "formValidator", formValidator);
        ReflectionTestUtils.setField(validator, "allEncounters", allEncounters);
    }

    @Test
    public void shouldValidateDeliveryForm() {

        DeliveryNotificationForm deliveryForm = new DeliveryNotificationForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        deliveryForm.setMotechId(motechId);
        deliveryForm.setFacilityId(facilityId);
        deliveryForm.setStaffId(staffId);

        final DependentValidator mockDependentValidator = mock(DependentValidator.class);
        when(validator.dependentValidator()).thenReturn(mockDependentValidator);

        PatientValidator expectedValidator = new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsFemale().onSuccess(new AgeMoreThan(5).onSuccess(new EnrolledToANC(allEncounters).onFailure(new RegANCFormSubmittedInSameUpload())))))
                .onFailure(new RegANCFormSubmittedInSameUpload().onFailure(new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForMother())));

        Patient patient = mock(Patient.class);
        when(formValidator.getPatient(motechId)).thenReturn(patient);

        List<FormBean> formBeans = Arrays.<FormBean>asList(deliveryForm);
        final FormBeanGroup formBeanGroup = new FormBeanGroup(formBeans);
        validator.validate(deliveryForm, formBeanGroup, formBeans);

        verify(formValidator).validateIfStaffExists(staffId);
        verify(formValidator).validateIfFacilityExists(facilityId);
        verify(mockDependentValidator).validate(patient, formBeans, formBeans, expectedValidator);
    }

    @Test
    public void shouldThrowErrorWhenPatientIsAChild() {
        DeliveryNotificationForm deliveryNotificationForm = new DeliveryNotificationForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        deliveryNotificationForm.setMotechId(motechId);

        //patient age less than 5
        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson().dead(false).age(4).gender("F"), new MRSFacility(facilityId)));
        when(formValidator.getPatient(motechId)).thenReturn(patient);
        List<FormBean> formBeans = Arrays.<FormBean>asList(deliveryNotificationForm);
        List<FormError> errors = validator.validate(deliveryNotificationForm, new FormBeanGroup(formBeans), formBeans);
        assertThat(errors, hasItem(new FormError("Patient age", "is less than 5")));
    }


}
