package org.motechproject.ghana.national.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Constants.NOT_FOUND;

public class FormValidatorTest {
    private FormValidator formValidator;

    @Mock
    private FacilityService facilityService;
    @Mock
    private StaffService staffService;

    @Before
    public void setUp() {
        initMocks(this);
        formValidator = new FormValidator();
        ReflectionTestUtils.setField(formValidator, "facilityService", facilityService);
        ReflectionTestUtils.setField(formValidator, "staffService", staffService);
    }

    @Test
    public void shouldValidateIfTheFacilityExists() {
        String facilityMotechId = "012345678";
        Facility facility = mock(Facility.class);
        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(null);
        List<FormError> formErrors = formValidator.validateIfFacilityExists(facilityMotechId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.FACILITY_ID, NOT_FOUND)));

        when(facilityService.getFacilityByMotechId(facilityMotechId)).thenReturn(facility);
        formErrors = formValidator.validateIfFacilityExists(facilityMotechId);
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.FACILITY_ID, NOT_FOUND))));
    }

    @Test
    public void shouldValidateIfStaffExists() {
        String staffId = "1234567";
        when(staffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(null);

        List<FormError> formErrors = formValidator.validateIfStaffExists(staffId);
        assertThat(formErrors, hasItem(new FormError(FormValidator.STAFF_ID, NOT_FOUND)));

        MRSUser staff = mock(MRSUser.class);
        when(staffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(staff);

        formErrors = formValidator.validateIfStaffExists(staffId);
        assertThat(formErrors, not(hasItem(new FormError(FormValidator.STAFF_ID, NOT_FOUND))));
    }
}
