package org.motechproject.ghana.national.validator;

import org.junit.Test;
import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Constants;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PncMotherFormValidatorTest {

    @Test
    public void shouldValidatePNCMotherForm() {
        PncMotherFormValidator pncMotherFormValidator = new PncMotherFormValidator();
        FormValidator mockFormValidator = mock(FormValidator.class);
        ReflectionTestUtils.setField(pncMotherFormValidator, "formValidator", mockFormValidator);

        PNCMotherForm pncMotherForm = new PNCMotherForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        pncMotherForm.setMotechId(motechId);
        pncMotherForm.setFacilityId(facilityId);
        pncMotherForm.setStaffId(staffId);
        pncMotherFormValidator.validate(pncMotherForm);
        verify(mockFormValidator).validateIfStaffExists(staffId);
        verify(mockFormValidator).validateIfFacilityExists(facilityId);
        verify(mockFormValidator).validateIfPatientExistsAndIsAlive(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
    }
}
