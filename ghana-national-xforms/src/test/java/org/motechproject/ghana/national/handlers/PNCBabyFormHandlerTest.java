package org.motechproject.ghana.national.handlers;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.ChildVisitService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.request.PNCBabyRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PNCBabyFormHandlerTest {

    @Test
    public void shouldHandlePNCBabyFormProcessing() {
        PNCBabyFormHandler pncBabyFormHandler = new PNCBabyFormHandler();
        FacilityService mockFacilityService = mock(FacilityService.class);
        StaffService mockStaffService = mock(StaffService.class);
        PatientService mockPatientService = mock(PatientService.class);
        ChildVisitService mockChildVisitService = mock(ChildVisitService.class);

        ReflectionTestUtils.setField(pncBabyFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(pncBabyFormHandler, "staffService", mockStaffService);
        ReflectionTestUtils.setField(pncBabyFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(pncBabyFormHandler, "childVisitService", mockChildVisitService);

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
        assertEquals(mockFacility, pncBabyRequest.getFacility());
        assertEquals(mockPatient, pncBabyRequest.getPatient());
        assertEquals(mockStaff, pncBabyRequest.getStaff());
    }
}

