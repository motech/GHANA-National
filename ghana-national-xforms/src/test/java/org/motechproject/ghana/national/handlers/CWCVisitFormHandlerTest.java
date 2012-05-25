package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CWCVisitForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.ChildVisitService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCVisitFormHandlerTest {

    private CWCVisitFormHandler handler;
    @Mock
    private ChildVisitService mockChildVisitService;
    @Mock
    private FacilityService mockFacilityService;
    @Mock
    private StaffService mockStaffService;
    @Mock
    private PatientService mockPatientService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new CWCVisitFormHandler();
        ReflectionTestUtils.setField(handler, "childVisitService", mockChildVisitService);
        ReflectionTestUtils.setField(handler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(handler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(handler, "staffService", mockStaffService);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(mockChildVisitService).save(Matchers.<CWCVisit>any());
        try {
            handler.handleFormEvent(new CWCVisitForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("Encountered error while processing CWC visit form"));
        }
    }

    @Test
    public void shouldCreateCWCVisitEncounterWithAllInfo() {
        final CWCVisitForm cwcVisitForm = new CWCVisitForm();
        cwcVisitForm.setStaffId("465");
        String motechId = "2321465";
        String staffId = "staffId";
        String motechFacilityId = "232465";
        cwcVisitForm.setFacilityId(motechFacilityId);
        cwcVisitForm.setStaffId(staffId);
        cwcVisitForm.setMotechId(motechId);
        cwcVisitForm.setDate(new Date());
        cwcVisitForm.setSerialNumber("4ds65");
        cwcVisitForm.setWeight(65.67d);
        cwcVisitForm.setHeight(173d);
        cwcVisitForm.setMuac(10d);
        cwcVisitForm.setImmunizations("LIST LIST LIST");
        cwcVisitForm.setComments("comments");
        cwcVisitForm.setCwcLocation("location");
        cwcVisitForm.setHouse("house");
        cwcVisitForm.setCommunity("community");
        cwcVisitForm.setMaleInvolved(false);


        MotechEvent motechEvent = new MotechEvent("form.validation.successful.NurseDataEntry.cwcVisit", new HashMap<String, Object>() {{
            put("formBean", cwcVisitForm);
        }});

        MRSUser staff = new MRSUser();
        staff.id(staffId);
        MRSFacility mrsFacility = new MRSFacility("111");
        Facility facility = new Facility(mrsFacility);
        facility.motechId(motechFacilityId);
        when(mockFacilityService.getFacilityByMotechId(motechFacilityId)).thenReturn(facility);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(staff);
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(new Patient(new MRSPatient(motechId, new MRSPerson(), mrsFacility)));
        
        handler.handleFormEvent(cwcVisitForm);

        ArgumentCaptor<CWCVisit> captor = ArgumentCaptor.forClass(CWCVisit.class);
        verify(mockChildVisitService).save(captor.capture());
        CWCVisit actualCWCVisit = captor.getValue();
        assertEquals(cwcVisitForm.getStaffId(), actualCWCVisit.getStaff().getId());
        assertEquals(cwcVisitForm.getFacilityId(), actualCWCVisit.getFacility().motechId());
        assertEquals(cwcVisitForm.getMotechId(), actualCWCVisit.getPatient().getMotechId());
        assertEquals(cwcVisitForm.getDate(), actualCWCVisit.getDate());
        assertEquals(cwcVisitForm.getSerialNumber(), actualCWCVisit.getSerialNumber());
        assertEquals(cwcVisitForm.getWeight(), actualCWCVisit.getWeight());
        assertEquals(cwcVisitForm.getComments(), actualCWCVisit.getComments());
        assertEquals(cwcVisitForm.getCwcLocation(), actualCWCVisit.getCwcLocation());
        assertEquals(cwcVisitForm.getHouse(), actualCWCVisit.getHouse());
        assertEquals(cwcVisitForm.getCommunity(), actualCWCVisit.getCommunity());
        assertEquals(cwcVisitForm.getMaleInvolved(), actualCWCVisit.getMaleInvolved());
    }
}
