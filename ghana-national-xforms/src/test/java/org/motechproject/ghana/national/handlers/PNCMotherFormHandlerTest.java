package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MotherVisitService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCMotherFormHandlerTest {
    private PNCMotherFormHandler pncMotherFormHandler;
    @Mock
    private MotherVisitService motherVisitService;
    @Mock
    private FacilityService facilityService;
    @Mock
    private PatientService patientService;
    @Mock
    private StaffService staffService;

    @Before
    public void setUp() {
        initMocks(this);
        pncMotherFormHandler = new PNCMotherFormHandler();
        ReflectionTestUtils.setField(pncMotherFormHandler, "motherVisitService", motherVisitService);
        ReflectionTestUtils.setField(pncMotherFormHandler, "facilityService", facilityService);
        ReflectionTestUtils.setField(pncMotherFormHandler, "staffService", staffService);
        ReflectionTestUtils.setField(pncMotherFormHandler, "patientService", patientService);
    }

    @Test
    public void shouldSavePNCMotherRequest() {
        String comment = "comment";
        String community = "community";
        Date date = DateUtil.now().toDate();
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
        String visitNumber = "1";
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

        Map<String, Object> params = new HashMap<String, Object>() {{
            put(Constants.FORM_BEAN, pncMotherForm);
        }};
        pncMotherFormHandler.handleFormEvent(new MotechEvent("subject", params));

        ArgumentCaptor<PNCMotherRequest> requestCaptor = ArgumentCaptor.forClass(PNCMotherRequest.class);
        verify(motherVisitService).save(requestCaptor.capture());
    }

    private PNCMotherForm createPNCMotherForm(String comment, String community, Date date, String facilityId, String staffId, String fht, String house, String location, Boolean lochiaAmountExcess, String lochiaColour,
                                              Boolean lochiaOdourFoul, Boolean maleInvolved, String motechId, double temperature, String visitNumber, String vitaminA) {
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
