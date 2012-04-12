package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ANCVisitForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MotherVisitService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCVisitFormHandlerTest {

    private ANCVisitFormHandler handler;
    @Mock
    private MotherVisitService mockMotherVisitService;
    @Mock
    private FacilityService mockFacilityService;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private StaffService mockStaffService;
    @Mock
    private AllSchedules mockAllSchedules;


    @Before
    public void setUp() {
        initMocks(this);
        handler = new ANCVisitFormHandler();
        ReflectionTestUtils.setField(handler, "visitService", mockMotherVisitService);
        ReflectionTestUtils.setField(handler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(handler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(handler, "staffService", mockStaffService);
        ReflectionTestUtils.setField(handler, "allSchedules", mockAllSchedules);
    }

    @Test
    public void shouldCreateANCVisitEncounterWithAllInfo() throws ObservationNotFoundException {
        String motechFacilityId = "232465";
        String staffId = "465";
        String motechId = "2321465";

        final ANCVisitForm ancVisitForm = new ANCVisitForm();
        ancVisitForm.setStaffId(staffId);
        ancVisitForm.setFacilityId(motechFacilityId);
        ancVisitForm.setMotechId(motechId);
        ancVisitForm.setDate(new Date());
        ancVisitForm.setSerialNumber("4ds65");
        ancVisitForm.setVisitNumber("4");
        ancVisitForm.setEstDeliveryDate(new Date(2012, 8, 8));
        ancVisitForm.setBpDiastolic(67);
        ancVisitForm.setBpSystolic(10);
        ancVisitForm.setWeight(65.67d);
        ancVisitForm.setComments("comments");
        ancVisitForm.setTtdose("4");
        ancVisitForm.setIptdose("56");
        ancVisitForm.setIptReactive(true);
        ancVisitForm.setItnUse("itn");
        ancVisitForm.setFht(4.3d);
        ancVisitForm.setFhr(4);
        ancVisitForm.setUrineTestGlucosePositive("glucose");
        ancVisitForm.setUrineTestProteinPositive("protein");
        ancVisitForm.setHemoglobin(13.8);
        ancVisitForm.setVdrlReactive("vdrl");
        ancVisitForm.setVdrlTreatment("treament");
        ancVisitForm.setDewormer("dewormer");
        ancVisitForm.setPmtct("pmctc");
        ancVisitForm.setPreTestCounseled("pre-test");
        ancVisitForm.setHivTestResult("hiv");
        ancVisitForm.setPostTestCounseled("post");
        ancVisitForm.setPmtctTreament("treatment");
        ancVisitForm.setLocation("location");
        ancVisitForm.setHouse("house");
        ancVisitForm.setCommunity("community");
        ancVisitForm.setReferred("referred");
        ancVisitForm.setMaleInvolved(false);
        ancVisitForm.setNextANCDate(new Date(2012, 2, 20));


        MotechEvent motechEvent = new MotechEvent("form.validation.successful.NurseDataEntry.ancVisit", new HashMap<String, Object>() {{
            put("formBean", ancVisitForm);
        }});

        String facilityId = "111";
        MRSFacility mrsFacility = new MRSFacility(facilityId);
        Patient patient = new Patient(new MRSPatient("patientId", motechId, new MRSPerson(), mrsFacility));
        MRSUser staff = new MRSUser();
        staff.id(staffId);
        Facility facility = new Facility(mrsFacility);
        facility.motechId(motechFacilityId);

        when(mockFacilityService.getFacilityByMotechId(motechFacilityId)).thenReturn(facility);
        when(mockPatientService.getPatientByMotechId(motechId)).thenReturn(patient);
        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(staff);
        
        handler.handleFormEvent(motechEvent);

        ArgumentCaptor<ANCVisitRequest> captor = ArgumentCaptor.forClass(ANCVisitRequest.class);
        verify(mockMotherVisitService).registerANCVisit(captor.capture());
        ANCVisitRequest actualANCVisitRequest = captor.getValue();
        assertEquals(ancVisitForm.getStaffId(), actualANCVisitRequest.getStaff().getId());
        assertEquals(motechFacilityId, actualANCVisitRequest.getFacility().getMotechId());
        assertEquals(ancVisitForm.getMotechId(), actualANCVisitRequest.getPatient().getMotechId());
        assertEquals(ancVisitForm.getDate(), actualANCVisitRequest.getDate());
        assertEquals(ancVisitForm.getSerialNumber(), actualANCVisitRequest.getSerialNumber());
        assertEquals(ancVisitForm.getVisitNumber(), actualANCVisitRequest.getVisitNumber());
        assertEquals(ancVisitForm.getEstDeliveryDate(), actualANCVisitRequest.getEstDeliveryDate());
        assertEquals(ancVisitForm.getBpDiastolic(), actualANCVisitRequest.getBpDiastolic());
        assertEquals(ancVisitForm.getBpSystolic(), actualANCVisitRequest.getBpSystolic());
        assertEquals(ancVisitForm.getWeight(), actualANCVisitRequest.getWeight());
        assertEquals(ancVisitForm.getComments(), actualANCVisitRequest.getComments());
        assertEquals(ancVisitForm.getTtdose(), actualANCVisitRequest.getTtdose());
        assertEquals(ancVisitForm.getIptdose(), actualANCVisitRequest.getIptdose());
        assertEquals(ancVisitForm.getIptReactive(), actualANCVisitRequest.getIptReactive());
        assertEquals(ancVisitForm.getItnUse(), actualANCVisitRequest.getItnUse());
        assertEquals(ancVisitForm.getFht(), actualANCVisitRequest.getFht());
        assertEquals(ancVisitForm.getFhr(), actualANCVisitRequest.getFhr());
        assertEquals(ancVisitForm.getUrineTestGlucosePositive(), actualANCVisitRequest.getUrineTestGlucosePositive());
        assertEquals(ancVisitForm.getUrineTestProteinPositive(), actualANCVisitRequest.getUrineTestProteinPositive());
        assertEquals(ancVisitForm.getHemoglobin(), actualANCVisitRequest.getHemoglobin());
        assertEquals(ancVisitForm.getVdrlReactive(), actualANCVisitRequest.getVdrlReactive());
        assertEquals(ancVisitForm.getVdrlTreatment(), actualANCVisitRequest.getVdrlTreatment());
        assertEquals(ancVisitForm.getDewormer(), actualANCVisitRequest.getDewormer());
        assertEquals(ancVisitForm.getPmtct(), actualANCVisitRequest.getPmtct());
        assertEquals(ancVisitForm.getPreTestCounseled(), actualANCVisitRequest.getPreTestCounseled());
        assertEquals(ancVisitForm.getHivTestResult(), actualANCVisitRequest.getHivTestResult());
        assertEquals(ancVisitForm.getPostTestCounseled(), actualANCVisitRequest.getPostTestCounseled());
        assertEquals(ancVisitForm.getPmtctTreament(), actualANCVisitRequest.getPmtctTreament());
        assertEquals(ancVisitForm.getLocation(), actualANCVisitRequest.getLocation());
        assertEquals(ancVisitForm.getHouse(), actualANCVisitRequest.getHouse());
        assertEquals(ancVisitForm.getCommunity(), actualANCVisitRequest.getCommunity());
        assertEquals(ancVisitForm.getReferred(), actualANCVisitRequest.getReferred());
        assertEquals(ancVisitForm.getMaleInvolved(), actualANCVisitRequest.getMaleInvolved());
        assertEquals(ancVisitForm.getNextANCDate(), actualANCVisitRequest.getNextANCDate());
    }
}
