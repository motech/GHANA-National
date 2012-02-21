package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ANCVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
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
        ANCVisitForm formBean = mock(ANCVisitForm.class);
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        validator.validate(formBean);

        verify(formValidator).validateIfStaffExists(staffId);
        verify(formValidator).validateIfFacilityExists(facilityId);
        verify(formValidator).validateIfPatientExistsAndIsAlive(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        verify(formValidator).validateIfPatientIsFemale(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        verify(mockAllEncounters).fetchLatest(motechId, ANC_REG_VISIT.value());
    }

    @Test
    public void shouldReturnFormErrorIfPatientIsFemaleButNotEnrolledForANC() {
        ANCVisitForm formBean = mock(ANCVisitForm.class);
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        when(mockAllEncounters.fetchLatest(motechId, ANC_REG_VISIT.value())).thenReturn(null);

        List<FormError> formErrors = validator.validate(formBean);

        assertThat(formErrors.size(), is(equalTo(1)));
        assertThat(formErrors.get(0).getParameter(), is(equalTo(motechId)));
    }

}
