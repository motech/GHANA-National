package org.motechproject.ghana.national.validator;

import org.junit.Test;
import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Constants;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PncBabyFormValidatorTest {

    @Test
    public void shouldValidatePNCBabyForm() {
        PncBabyFormValidator pncBabyFormValidator = new PncBabyFormValidator();
        FormValidator mockFormValidator = mock(FormValidator.class);
        ReflectionTestUtils.setField(pncBabyFormValidator, "formValidator", mockFormValidator);

        PNCBabyForm pncBabyForm = new PNCBabyForm();
        String motechId = "motechId";
        String facilityId = "facilityId";
        String staffId = "staffId";
        pncBabyForm.setMotechId(motechId);
        pncBabyForm.setFacilityId(facilityId);
        pncBabyForm.setStaffId(staffId);
        pncBabyFormValidator.validate(pncBabyForm);
        verify(mockFormValidator).validateIfStaffExists(staffId);
        verify(mockFormValidator).validateIfFacilityExists(facilityId);
        verify(mockFormValidator).validateIfPatientExistsAndIsAlive(motechId, Constants.MOTECH_ID_ATTRIBUTE_NAME);
        verify(mockFormValidator).validateIfPatientIsAChild(motechId);
    }
}
