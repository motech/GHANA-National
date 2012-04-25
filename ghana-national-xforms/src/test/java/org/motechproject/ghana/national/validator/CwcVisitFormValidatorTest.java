package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CWCVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
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
        CWCVisitForm formBean = mock(CWCVisitForm.class);
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        validator.validate(formBean);

        verify(formValidator).validateIfStaffExists(staffId);
        verify(formValidator).validateIfFacilityExists(facilityId);
        verify(formValidator).validateIfPatientIsAliveAndIsAChild(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        verify(mockAllEncounters).getLatest(motechId, CWC_REG_VISIT.value());
    }

    @Test
    public void shouldReturnFormErrorIfPatientIsFemaleButNotEnrolledForCWC() {
        CWCVisitForm formBean = mock(CWCVisitForm.class);
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        when(mockAllEncounters.getLatest(motechId, CWC_REG_VISIT.value())).thenReturn(null);

        List<FormError> formErrors = validator.businessValidations(formBean, new ArrayList<FormError>());

        assertThat(formErrors.size(), is(equalTo(1)));
        assertThat(formErrors.get(0).getParameter(), is(equalTo(motechId)));
    }

}
