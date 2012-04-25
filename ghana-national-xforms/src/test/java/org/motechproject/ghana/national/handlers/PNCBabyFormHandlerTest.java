package org.motechproject.ghana.national.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.ChildVisitService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.request.PNCBabyRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSUser;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class PNCBabyFormHandlerTest {

    PNCBabyFormHandler pncBabyFormHandler;
    FacilityService mockFacilityService;
    StaffService mockStaffService;
    PatientService mockPatientService;
    ChildVisitService mockChildVisitService;

    @Before
    public void setup() {
        pncBabyFormHandler = new PNCBabyFormHandler();
        mockFacilityService = mock(FacilityService.class);
        mockStaffService = mock(StaffService.class);
        mockPatientService = mock(PatientService.class);
        mockChildVisitService = mock(ChildVisitService.class);

        setField(pncBabyFormHandler, "facilityService", mockFacilityService);
        setField(pncBabyFormHandler, "staffService", mockStaffService);
        setField(pncBabyFormHandler, "patientService", mockPatientService);
        setField(pncBabyFormHandler, "childVisitService", mockChildVisitService);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(mockChildVisitService).save(Matchers.<PNCBabyRequest>any());
        try {
            pncBabyFormHandler.handleFormEvent(new MotechEvent("subject"));
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("subject"));
        }
    }

    @Test
    public void shouldHandlePNCBabyFormProcessing() {
        String facilityId = "motechFacilityId";
        String staffId = "staffId";
        String patientId = "patientId";
        String comments = "comments";
        String community = "community";
        String house = "house";
        String location = "location";
        Boolean babyCondition = Boolean.TRUE;
        Boolean bcg = Boolean.TRUE;
        Boolean opv = Boolean.TRUE;
        Boolean cordCondition = Boolean.FALSE;
        DateTime pncDate = DateTime.now();
        int visitNumber = 2;
        Facility mockFacility = mock(Facility.class);
        Patient mockPatient = mock(Patient.class);
        MRSUser mockStaff = mock(MRSUser.class);

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        PNCBabyForm babyForm = new PNCBabyForm();
        babyForm.setFacilityId(facilityId);
        babyForm.setMotechId(patientId);
        babyForm.setStaffId(staffId);
        babyForm.setComments(comments);
        babyForm.setCommunity(community);
        babyForm.setHouse(house);
        babyForm.setBabyConditionGood(babyCondition);
        babyForm.setCordConditionNormal(cordCondition);
        babyForm.setBcg(bcg);
        babyForm.setOpv0(opv);
        babyForm.setLocation(location);
        babyForm.setDate(pncDate);
        babyForm.setVisitNumber(visitNumber);
        parameters.put(Constants.FORM_BEAN, babyForm);

        when(mockFacilityService.getFacilityByMotechId(facilityId)).thenReturn(mockFacility);
        when(mockPatientService.getPatientByMotechId(patientId)).thenReturn(mockPatient);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mockStaff);

        pncBabyFormHandler.handleFormEvent(new MotechEvent("subject", parameters));

        verify(mockFacilityService).getFacilityByMotechId(facilityId);
        verify(mockStaffService).getUserByEmailIdOrMotechId(staffId);
        verify(mockPatientService).getPatientByMotechId(patientId);

        ArgumentCaptor<PNCBabyRequest> captor = ArgumentCaptor.forClass(PNCBabyRequest.class);
        verify(mockChildVisitService).save(captor.capture());

        PNCBabyRequest pncBabyRequest = captor.getValue();

        assertEquals(comments, pncBabyRequest.getComments());
        assertEquals(community, pncBabyRequest.getCommunity());
        assertEquals(house, pncBabyRequest.getHouse());
        assertEquals(location, pncBabyRequest.getLocation());
        assertEquals(babyCondition, pncBabyRequest.getBabyConditionGood());
        assertEquals(bcg, pncBabyRequest.getBcg());
        assertEquals(opv, pncBabyRequest.getOpv0());
        assertEquals(cordCondition, pncBabyRequest.getCordConditionNormal());
        assertEquals(pncDate, pncBabyRequest.getDate());
        assertEquals(visitNumber, pncBabyRequest.getVisit().visitNumber());
        assertEquals(mockFacility, pncBabyRequest.getFacility());
        assertEquals(mockPatient, pncBabyRequest.getPatient());
        assertEquals(mockStaff, pncBabyRequest.getStaff());
    }
}

