package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.CWCEnrollment;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.MotechProgram;
import org.motechproject.ghana.national.matcher.CWCEnrollmentFormMatcher;
import org.motechproject.ghana.national.service.EnrollmentService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.vo.MotechProgramName;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ModelMap;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCEnrollmentControllerTest {

    CWCEnrollmentController controller;

    @Mock
    EnrollmentService mockEnrollmentService;
    @Mock
    FacilityHelper facilityHelper;
    @Mock
    private FacilityService mockFacilityService;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new CWCEnrollmentController(mockEnrollmentService, facilityHelper, mockFacilityService);
    }

    @Test
    public void shouldRenderEnrollCWCPage() {
        String patientId = "1243";
        ModelMap modelMap = new ModelMap();
        String facilityId = "FacilityId";
        CWCEnrollment CWCEnrollment = mock(CWCEnrollment.class);
        Facility facility = mock(Facility.class);

        when(CWCEnrollment.getFacilityId()).thenReturn(facilityId);
        when(mockEnrollmentService.cwcEnrollmentFor(patientId)).thenReturn(CWCEnrollment);
        when(mockFacilityService.getFacilityByMotechId(facilityId)).thenReturn(facility);

        assertThat(controller.enrollCWC(patientId, modelMap), is(equalTo("enroll/cwc")));
        CWCEnrollmentForm cwcEnrollmentForm = (CWCEnrollmentForm) modelMap.get("cwcEnrollmentForm");
        assertThat(cwcEnrollmentForm, is(new CWCEnrollmentFormMatcher(cwcEnrollmentForm)));
        verify(facilityHelper).locationMap();
    }

    @Test
    public void shouldSaveCWCEnrollmentOfAPatientWhenEnrollmentDoesNotExist() {
        String facilityId = "facilityId";
        String patientId = "motechIdForPatient";
        MotechProgram motechProgram = new MotechProgram(MotechProgramName.CWC);
        Date registrationDate = DateUtil.now().toDate();
        String serialNumber = "someRandomNumber";
        Facility mockFacility = mock(Facility.class);

        FacilityForm mockFacilityForm = mock(FacilityForm.class);
        when(mockFacilityForm.getFacilityId()).thenReturn(facilityId);

        ModelMap modelMap = new ModelMap();
        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm(null, mockFacility);
        cwcEnrollmentForm.setPatientMotechId(patientId);
        cwcEnrollmentForm.setRegistrationDate(registrationDate);
        cwcEnrollmentForm.setSerialNumber(serialNumber);
        cwcEnrollmentForm.setFacilityForm(mockFacilityForm);

        controller.enrollCWC(cwcEnrollmentForm , modelMap);

        ArgumentCaptor<CWCEnrollment> captor = ArgumentCaptor.forClass(CWCEnrollment.class);
        verify(mockEnrollmentService).saveOrUpdate(captor.capture());
        CWCEnrollment CWCEnrollment = captor.getValue();
        assertEnrollment(facilityId, patientId, motechProgram, registrationDate, CWCEnrollment);
    }

    private void assertEnrollment(String facilityId, String patientId, MotechProgram motechProgram, Date registrationDate, CWCEnrollment CWCEnrollment) {
        assertThat(CWCEnrollment.getFacilityId(), is(equalTo(facilityId)));
        assertThat(CWCEnrollment.getPatientId(), is(equalTo(patientId)));
        assertThat(CWCEnrollment.getProgram().getName(), is(equalTo(motechProgram.getName())));
        assertThat(CWCEnrollment.getRegistrationDate(), is(equalTo(registrationDate)));
    }
}
