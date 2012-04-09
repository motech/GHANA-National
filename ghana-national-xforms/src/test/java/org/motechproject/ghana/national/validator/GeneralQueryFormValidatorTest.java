package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.GeneralQueryType;
import org.motechproject.ghana.national.bean.GeneralQueryForm;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;


public class GeneralQueryFormValidatorTest {

    private GeneralQueryFormValidator generalQueryFormValidator;
    @Mock
    private FormValidator formValidator;
    @Before
    public void setup(){
        initMocks(this);
        generalQueryFormValidator=new GeneralQueryFormValidator();
        ReflectionTestUtils.setField(generalQueryFormValidator, "formValidator", formValidator);
    }
    
    @Test
    public void shouldValidateTheGeneralQueryFormBean() {
        String facilityId = "13161";
        String staffId = "23";
        String responsePhoneNumber="0987654321";
        GeneralQueryType queryType= GeneralQueryType.ANC_DEFAULTERS;
        GeneralQueryForm generalQueryForm = createGeneralQueryForm(facilityId, staffId, responsePhoneNumber, queryType);

        when(formValidator.validateIfFacilityExists(facilityId)).thenReturn(asList(new FormError(facilityId, NOT_FOUND)));
        when(formValidator.validateIfStaffExists(staffId)).thenReturn(asList(new FormError(staffId, NOT_FOUND)));

        List<FormError> formErrors = generalQueryFormValidator.validate(generalQueryForm);

        assertThat(formErrors, hasItem(new FormError(facilityId, NOT_FOUND)));
        assertThat(formErrors, hasItem(new FormError(staffId, NOT_FOUND)));
    }

    private GeneralQueryForm createGeneralQueryForm(String facilityId, String staffId, String responsePhoneNumber, GeneralQueryType queryType) {
        GeneralQueryForm generalQueryForm = new GeneralQueryForm();
        generalQueryForm.setFacilityId(facilityId);
        generalQueryForm.setStaffId(staffId);
        generalQueryForm.setResponsePhoneNumber(responsePhoneNumber);
        generalQueryForm.setQueryType(queryType);
        return generalQueryForm;
    }
}
