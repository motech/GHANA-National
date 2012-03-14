package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.*;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PregnancyTerminationFormHandlerTest {
    private PregnancyTerminationFormHandler pregnancyTerminationFormHandler;


    @Mock
    PregnancyService mockPregnancyService;

    @Mock
    PatientService mockPatientService;

    @Mock
    FacilityService mockFacilityService;

    @Mock
    StaffService mockStaffService;

    @Before
    public void setUp() {
        initMocks(this);
        pregnancyTerminationFormHandler = new PregnancyTerminationFormHandler();
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler, "pregnancyService", mockPregnancyService);
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(pregnancyTerminationFormHandler, "staffService", mockStaffService);
    }

    @Test
    public void shouldHandlePregnancyTerminationEvent() {
        String facilityId = "facilityId";
        String staffId = "staffId";
        String motechId = "motechId";
        PregnancyTerminationForm form = pregnancyTerminationForm(motechId, staffId, facilityId);

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Constants.FORM_BEAN, form);
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.PregnancyTermination", parameters);

        pregnancyTerminationFormHandler.handleFormEvent(event);

        verify(mockPregnancyService).terminatePregnancy(any(PregnancyTerminationRequest.class));

    }

    @Test
    public void shouldCreateRequestObjectFromFormBean() {
        Date terminationDate = DateUtil.today().toDate();
        String facilityId = "facilityId";
        String motechId = "motechId";
        String staffId = "staffId";

        PregnancyTerminationForm form = new PregnancyTerminationForm();
        form.setComments("Comment on death");
        form.setComplications("Bleeding");
        form.setDate(terminationDate);
        form.setFacilityId(facilityId);
        form.setMaternalDeath(Boolean.TRUE);
        form.setMotechId(motechId);
        form.setProcedure("1");
        form.setReferred(Boolean.TRUE);
        form.setStaffId(staffId);
        form.setTerminationType("TerminationType");
        form.setPostAbortionFPAccepted(Boolean.TRUE);
        form.setPostAbortionFPCounseled(Boolean.TRUE);

        Facility mockFacility = mock(Facility.class);
        MRSUser mockStaff = mock(MRSUser.class);
        Patient mockPatient = mock(Patient.class);
        when(mockFacilityService.getFacilityByMotechId(facilityId)).thenReturn(mockFacility);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mockStaff);
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(mockPatient);
        PregnancyTerminationRequest request = pregnancyTerminationFormHandler.createPregnancyTerminationVisit(form);

        assertEquals(form.getComments(), request.getComments());
        assertReflectionEquals(form.getTerminationComplications(), request.getComplications());
        assertEquals(form.getDate(), request.getTerminationDate());
        assertEquals(mockFacility, request.getFacility());
        assertEquals(form.getMaternalDeath(), request.isDead());
        assertEquals(mockPatient, request.getPatient());
        assertEquals(form.getProcedure(), request.getTerminationProcedure());
        assertEquals(form.getReferred(), request.isReferred());
        assertEquals(mockStaff, request.getStaff());
        assertEquals(form.getTerminationType(), request.getTerminationType());
        assertEquals(form.getPostAbortionFPAccepted(), request.getPostAbortionFPAccepted());
        assertEquals(form.getPostAbortionFPCounseled(), request.getPostAbortionFPCounselling());
    }

    private PregnancyTerminationForm pregnancyTerminationForm(String motechId, String staffId, String facilityId) {
        PregnancyTerminationForm pregnancyTerminationForm = new PregnancyTerminationForm();
        pregnancyTerminationForm.setFacilityId(facilityId);
        pregnancyTerminationForm.setMotechId(motechId);
        pregnancyTerminationForm.setStaffId(staffId);
        return pregnancyTerminationForm;
    }
}
