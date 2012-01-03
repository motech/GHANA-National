package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.RegisterANCForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegisterANCFormValidatorTest {
    private RegisterANCFormValidator registerANCFormValidator;

    @Mock
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @Before
    public void setUp(){
        initMocks(this);
        registerANCFormValidator = new RegisterANCFormValidator();
        ReflectionTestUtils.setField(registerANCFormValidator, "formValidator", formValidator);
    }

    @Test
    public void shouldValidateRegisterANCForm() {
        RegisterANCForm formBean = mock(RegisterANCForm.class);
        String motechId = "1231231";
        String staffId = "11";
        String facilityId = "34";
        when(formBean.getMotechId()).thenReturn(motechId);
        when(formBean.getStaffId()).thenReturn(staffId);
        when(formBean.getFacilityId()).thenReturn(facilityId);

        registerANCFormValidator.validate(formBean);

        verify(formValidator).validatePatient(Matchers.<List<FormError>>any(), eq(motechId));
        verify(formValidator).validateIfStaffExists(Matchers.<List<FormError>>any(), eq(staffId));
        verify(formValidator).validateIfFacilityExists(Matchers.<List<FormError>>any(), eq(facilityId));
    }
}
