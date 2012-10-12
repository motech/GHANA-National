package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ANCVisitForm;
import org.motechproject.ghana.national.domain.ANCVisit;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.AllANCVisitsForVisitor;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MotherVisitService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCVisitFormHandlerTest {

    @InjectMocks
    private ANCVisitFormHandler handler = new ANCVisitFormHandler();
    @Mock
    private MotherVisitService mockMotherVisitService;
    @Mock
    private FacilityService mockFacilityService;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private StaffService mockStaffService;
    @Mock
    private AllCareSchedules mockAllCareSchedules;
    @Mock
    private AllANCVisitsForVisitor mockAllANCVisitsForVisitor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldRethrowException() throws ObservationNotFoundException {
        doThrow(new RuntimeException()).when(mockMotherVisitService).registerANCVisit(Matchers.<ANCVisit>any());
        try {
            handler.handleFormEvent(new ANCVisitForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("Encountered error while processing ANC visit form"));
        }
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
        ancVisitForm.setVisitor(false);
        ancVisitForm.setNextANCDate(new Date(2012, 2, 20));

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

        handler.handleFormEvent(ancVisitForm);

        ArgumentCaptor<ANCVisit> captor = ArgumentCaptor.forClass(ANCVisit.class);
        verify(mockMotherVisitService).registerANCVisit(captor.capture());
        ANCVisit actualANCVisit = captor.getValue();
        assertEquals(ancVisitForm.getStaffId(), actualANCVisit.getStaff().getId());
        assertEquals(motechFacilityId, actualANCVisit.getFacility().getMotechId());
        assertEquals(ancVisitForm.getMotechId(), actualANCVisit.getPatient().getMotechId());
        assertEquals(ancVisitForm.getDate(), actualANCVisit.getDate());
        assertEquals(ancVisitForm.getSerialNumber(), actualANCVisit.getSerialNumber());
        assertEquals(ancVisitForm.getVisitNumber(), actualANCVisit.getVisitNumber());
        assertEquals(ancVisitForm.getEstDeliveryDate(), actualANCVisit.getEstDeliveryDate());
        assertEquals(ancVisitForm.getBpDiastolic(), actualANCVisit.getBpDiastolic());
        assertEquals(ancVisitForm.getBpSystolic(), actualANCVisit.getBpSystolic());
        assertEquals(ancVisitForm.getWeight(), actualANCVisit.getWeight());
        assertEquals(ancVisitForm.getComments(), actualANCVisit.getComments());
        assertEquals(ancVisitForm.getTtdose(), actualANCVisit.getTtdose());
        assertEquals(ancVisitForm.getIptdose(), actualANCVisit.getIptdose());
        assertEquals(ancVisitForm.getIptReactive(), actualANCVisit.getIptReactive());
        assertEquals(ancVisitForm.getItnUse(), actualANCVisit.getItnUse());
        assertEquals(ancVisitForm.getFht(), actualANCVisit.getFht());
        assertEquals(ancVisitForm.getFhr(), actualANCVisit.getFhr());
        assertEquals(ancVisitForm.getUrineTestGlucosePositive(), actualANCVisit.getUrineTestGlucosePositive());
        assertEquals(ancVisitForm.getUrineTestProteinPositive(), actualANCVisit.getUrineTestProteinPositive());
        assertEquals(ancVisitForm.getHemoglobin(), actualANCVisit.getHemoglobin());
        assertEquals(ancVisitForm.getVdrlReactive(), actualANCVisit.getVdrlReactive());
        assertEquals(ancVisitForm.getVdrlTreatment(), actualANCVisit.getVdrlTreatment());
        assertEquals(ancVisitForm.getDewormer(), actualANCVisit.getDewormer());
        assertEquals(ancVisitForm.getPmtct(), actualANCVisit.getPmtct());
        assertEquals(ancVisitForm.getPreTestCounseled(), actualANCVisit.getPreTestCounseled());
        assertEquals(ancVisitForm.getHivTestResult(), actualANCVisit.getHivTestResult());
        assertEquals(ancVisitForm.getPostTestCounseled(), actualANCVisit.getPostTestCounseled());
        assertEquals(ancVisitForm.getPmtctTreament(), actualANCVisit.getPmtctTreament());
        assertEquals(ancVisitForm.getLocation(), actualANCVisit.getLocation());
        assertEquals(ancVisitForm.getHouse(), actualANCVisit.getHouse());
        assertEquals(ancVisitForm.getCommunity(), actualANCVisit.getCommunity());
        assertEquals(ancVisitForm.getReferred(), actualANCVisit.getReferred());
        assertEquals(ancVisitForm.getMaleInvolved(), actualANCVisit.getMaleInvolved());
        assertEquals(ancVisitForm.getNextANCDate(), actualANCVisit.getNextANCDate());
        assertEquals(ancVisitForm.getVisitor(), actualANCVisit.getVisitor());
    }

    @Test
    public void shouldNotRegisterForANCVisitIfPatientIsAVisitor() {
        ANCVisitForm ancVisitForm = new ANCVisitForm();
        ancVisitForm.setVisitor(true);
        handler.handleFormEvent(ancVisitForm);

        verifyZeroInteractions(mockMotherVisitService);
        verify(mockAllANCVisitsForVisitor).save(Matchers.<ANCVisit>any());
    }
}
