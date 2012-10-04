package org.motechproject.ghana.national.handlers;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MotherVisitService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.VisitService;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCMotherFormHandlerTest {

    @InjectMocks
    private PNCMotherFormHandler pncMotherFormHandler = new PNCMotherFormHandler();
    @Mock
    private MotherVisitService motherVisitService;
    @Mock
    private FacilityService facilityService;
    @Mock
    private PatientService patientService;
    @Mock
    private StaffService staffService;
    @Mock
    private VisitService visitService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(motherVisitService).enrollOrFulfillPNCSchedulesForMother(Matchers.<PNCMotherRequest>any());
        try {
            pncMotherFormHandler.handleFormEvent(new PNCMotherForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.is("Exception occurred while processing PNC Mother form"));
        }
    }

    @Test
    public void shouldSavePNCMotherRequest() {
        String comment = "comment";
        String community = "community";
        DateTime date = DateUtil.now();
        String facilityId = "facilityId";
        String fht = "120";
        String house = "house";
        String location = "location";
        Boolean lochiaAmountExcess = Boolean.TRUE;
        String lochiaColour = "1";
        Boolean lochiaOdourFoul = Boolean.TRUE;
        Boolean maleInvolved = Boolean.TRUE;
        String motechId = "motechId";
        double temperature = 20D;
        int visitNumber = 1;
        String vitaminA = "vitaminA";
        String staffId = "staffId";
        Boolean referred = Boolean.TRUE;
        String ttDose = "1";

        Facility facility = new Facility(new MRSFacility(facilityId));
        Patient patient = mock(Patient.class);
        MRSUser mrsUser = mock(MRSUser.class);

        when(facilityService.getFacilityByMotechId(facilityId)).thenReturn(facility);
        when(patientService.getPatientByMotechId(motechId)).thenReturn(patient);
        when(staffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mrsUser);
        final PNCMotherForm pncMotherForm = createPNCMotherForm(comment, community, date, facilityId, staffId,
                fht, house, location, lochiaAmountExcess, lochiaColour, lochiaOdourFoul, maleInvolved, motechId, temperature, visitNumber, vitaminA);
        pncMotherForm.setReferred(referred);
        pncMotherForm.setTtDose(ttDose);

        pncMotherFormHandler.handleFormEvent(pncMotherForm);

        ArgumentCaptor<TTVaccine> ttVaccineCaptor = ArgumentCaptor.forClass(TTVaccine.class);

        verify(visitService).createTTSchedule(ttVaccineCaptor.capture());
        verify(motherVisitService).enrollOrFulfillPNCSchedulesForMother(any(PNCMotherRequest.class));

        TTVaccine vaccine = ttVaccineCaptor.getValue();
        assertThat(vaccine.getDosage(), is(TTVaccineDosage.TT1));
        assertThat(vaccine.getPatient(), is(patient));
    }

    private PNCMotherForm createPNCMotherForm(String comment, String community, DateTime date, String facilityId, String staffId, String fht, String house, String location, Boolean lochiaAmountExcess, String lochiaColour,
                                              Boolean lochiaOdourFoul, Boolean maleInvolved, String motechId, double temperature, int visitNumber, String vitaminA) {
        final PNCMotherForm pncMotherForm = new PNCMotherForm();
        pncMotherForm.setComments(comment);
        pncMotherForm.setCommunity(community);
        pncMotherForm.setDate(date);
        pncMotherForm.setFacilityId(facilityId);
        pncMotherForm.setFht(fht);
        pncMotherForm.setHouse(house);
        pncMotherForm.setLocation(location);
        pncMotherForm.setLochiaAmountExcess(lochiaAmountExcess);
        pncMotherForm.setLochiaColour(lochiaColour);
        pncMotherForm.setLochiaOdourFoul(lochiaOdourFoul);
        pncMotherForm.setStaffId(staffId);
        pncMotherForm.setMaleInvolved(maleInvolved);
        pncMotherForm.setReferred(Boolean.TRUE);
        pncMotherForm.setMotechId(motechId);
        pncMotherForm.setTemperature(temperature);
        pncMotherForm.setVisitNumber(visitNumber);
        pncMotherForm.setVitaminA(vitaminA);
        return pncMotherForm;
    }
}
