package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.TTVisitForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.CareVisitService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT1;

public class TTVisitFormHandlerTest {

    @Mock
    private CareVisitService ttVaccinationService;
    @Mock
    private PatientService patientService;
    @Mock
    private FacilityService facilityService;
    private TTVisitFormHandler ttVisitFormHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVisitFormHandler = new TTVisitFormHandler(ttVaccinationService, patientService, facilityService);
    }

    @Test
    public void shouldEnrollToReceiveSchedulesAndCreateVisitEncounter() {
        final TTVisitForm ttVisitForm = setupTTVisitForm();

        MotechEvent eventMock = new MotechEvent("", new HashMap<String, Object>() {{
            put(Constants.FORM_BEAN, ttVisitForm);
        }});

        Patient patient = mock(Patient.class);
        when(patientService.getPatientByMotechId(ttVisitForm.getMotechId())).thenReturn(patient);

        Facility facility = new Facility(new MRSFacility("mrs facility id"));
        when(facilityService.getFacilityByMotechId(ttVisitForm.getFacilityId())).thenReturn(facility);
        ttVisitFormHandler.handleFormEvent(eventMock);

        verify(ttVaccinationService).receivedTT(TT1, patient, ttVisitForm.getStaffId(), facility.getMrsFacilityId(), DateUtil.newDate(ttVisitForm.getDate()));
    }

    private TTVisitForm setupTTVisitForm() {
        final TTVisitForm ttVisitForm = new TTVisitForm();
        ttVisitForm.setFacilityId("FacilityId");
        ttVisitForm.setMotechId("PatientId");
        ttVisitForm.setDate(DateUtil.newDate(2000, 1, 1).toDate());
        ttVisitForm.setTtDose(String.valueOf(TT1.getDosage()));
        return ttVisitForm;
    }
}
