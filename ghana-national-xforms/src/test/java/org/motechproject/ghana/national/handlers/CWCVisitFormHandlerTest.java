package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.CWCVisitForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.ChildVisitService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CWCVisitFormHandlerTest {

    private CWCVisitFormHandler handler;
    @Mock
    private ChildVisitService childVisitService;
    @Mock
    private FacilityService mockFacilityService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new CWCVisitFormHandler();
        ReflectionTestUtils.setField(handler, "visitService", childVisitService);
        ReflectionTestUtils.setField(handler, "facilityService", mockFacilityService);

    }

    @Test
    public void shouldCreateCWCVisitEncounterWithAllInfo() {
        final CWCVisitForm cwcVisitForm = new CWCVisitForm();
        cwcVisitForm.setStaffId("465");
        String motechFacilityId = "232465";
        cwcVisitForm.setFacilityId(motechFacilityId);
        cwcVisitForm.setMotechId("2321465");
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

        String facilityId = "111";
        when(mockFacilityService.getFacilityByMotechId(motechFacilityId)).thenReturn(new Facility(new MRSFacility(facilityId)));
        
        handler.handleFormEvent(motechEvent);

        ArgumentCaptor<CWCVisit> captor = ArgumentCaptor.forClass(CWCVisit.class);
        verify(childVisitService).registerCWCVisit(captor.capture());
        CWCVisit actualCWCVisit = captor.getValue();
        assertEquals(cwcVisitForm.getStaffId(), actualCWCVisit.getStaffId());
        assertEquals(facilityId, actualCWCVisit.getFacilityId());
        assertEquals(cwcVisitForm.getMotechId(), actualCWCVisit.getMotechId());
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
