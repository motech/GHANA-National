package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.DeliveryForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.validator.patient.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeliveryFormValidatorTest {

    @Mock
    FormValidator formValidator;
    @Mock
    private AllEncounters allEncounters;

    DeliveryFormValidator deliveryFormValidator;

    @Before
    public void setUp() {
        initMocks(this);
        deliveryFormValidator = spy(new DeliveryFormValidator());
        ReflectionTestUtils.setField(deliveryFormValidator, "formValidator", formValidator);
        ReflectionTestUtils.setField(deliveryFormValidator, "allEncounters", allEncounters);
    }

    @Test
    public void shouldValidateDeliveryForm() {

        DeliveryForm deliveryForm = new DeliveryForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        deliveryForm.setMotechId(motechId);
        deliveryForm.setFacilityId(facilityId);
        deliveryForm.setStaffId(staffId);

        final DependentValidator mockDependentValidator = mock(DependentValidator.class);
        when(deliveryFormValidator.dependentValidator()).thenReturn(mockDependentValidator);

        PatientValidator expectedValidator = new ExistsInDb().onSuccess(new IsAlive().onSuccess(new IsFemale().onSuccess(new EnrolledToANC(allEncounters).onFailure(new RegANCFormSubmittedInSameUpload()))))
                .onFailure(new RegANCFormSubmittedInSameUpload().onFailure(new RegClientFormSubmittedInSameUpload().onSuccess(new RegClientFormSubmittedForMother())));

        Patient patient = mock(Patient.class);
        when(formValidator.getPatient(motechId)).thenReturn(patient);

        final FormBeanGroup formBeanGroup = new FormBeanGroup(Collections.<FormBean>emptyList());
        deliveryFormValidator.validate(deliveryForm, formBeanGroup);

        verify(formValidator).validateIfStaffExists(staffId);
        verify(formValidator).validateIfFacilityExists(facilityId);
        verify(mockDependentValidator).validate(patient, Collections.<FormBean>emptyList(), expectedValidator);
    }
}
