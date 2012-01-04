package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Enrollment;
import org.motechproject.ghana.national.service.EnrollmentService;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.springframework.ui.ModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnrollmentControllerTest {

    EnrollmentController controller;

    @Mock
    EnrollmentService mockEnrollmentService;
    @Mock
    FacilityHelper facilityHelper;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new EnrollmentController(mockEnrollmentService, facilityHelper);
    }

    @Test
    public void shouldRenderEnrollCWCPage() {
        String patientId = "1243";
        ModelMap modelMap = new ModelMap();
        Enrollment enrollment = mock(Enrollment.class);
        when(mockEnrollmentService.enrollmentFor(patientId)).thenReturn(enrollment);
        assertThat(controller.enrollCWC(patientId, modelMap), is(equalTo("enroll/cwc")));
        verify(facilityHelper).locationMap();
    }

//    In Progress
//    @Test
//    public void shouldSaveCWCEnrollmentOfAPatient() {
//        ModelMap modelMap = new ModelMap();
//        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm();
//        String patientId = "123456";
//        Enrollment mockEnrollment = mock(Enrollment.class);
//        when(mockEnrollmentService.enrollmentFor(patientId)).thenReturn(mockEnrollment);
//
//        controller.enrollCWC(cwcEnrollmentForm,modelMap);
//
//
//    }
}
