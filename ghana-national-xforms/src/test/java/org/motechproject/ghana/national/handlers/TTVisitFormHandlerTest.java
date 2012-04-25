package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.TTVisitForm;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.VisitService;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT1;

public class TTVisitFormHandlerTest {

    @Mock
    private VisitService ttVaccinationService;
    @Mock
    private PatientService patientService;
    @Mock
    private StaffService staffService;
    @Mock
    private FacilityService facilityService;
    private TTVisitFormHandler ttVisitFormHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ttVisitFormHandler = new TTVisitFormHandler(ttVaccinationService, patientService, facilityService, staffService);
    }

    @Test
    public void shouldRethrowException() {
        doThrow(new RuntimeException()).when(ttVaccinationService).receivedTT(Matchers.<TTVaccine>any(), Matchers.<MRSUser>any(), Matchers.<Facility>any());
        try {
            ttVisitFormHandler.handleFormEvent(new MotechEvent("subject"));
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("subject"));
        }
    }

    @Test
    public void shouldEnrollToReceiveSchedulesAndCreateVisitEncounter() {
        final TTVisitForm ttVisitForm = setupTTVisitForm(DateUtil.newDate(2000, 1, 1).toDate(), TT1);

        MotechEvent eventMock = new MotechEvent("", new HashMap<String, Object>() {{
            put(Constants.FORM_BEAN, ttVisitForm);
        }});

        Patient patient = new Patient(new MRSPatient("mrsPatientId"));
        MRSUser staff = new MRSUser();
        when(patientService.getPatientByMotechId(ttVisitForm.getMotechId())).thenReturn(patient);
        when(staffService.getUserByEmailIdOrMotechId(ttVisitForm.getStaffId())).thenReturn(staff);

        Facility facility = new Facility(new MRSFacility("mrs facility id"));
        when(facilityService.getFacilityByMotechId(ttVisitForm.getFacilityId())).thenReturn(facility);
        ttVisitFormHandler.handleFormEvent(eventMock);

        ArgumentCaptor<TTVaccine> ttVaccineArgumentCaptor = ArgumentCaptor.forClass(TTVaccine.class);
        verify(ttVaccinationService).receivedTT(ttVaccineArgumentCaptor.capture(), eq(staff), eq(facility));

        assertThat(ttVaccineArgumentCaptor.getValue().getVaccinationDate(), is(equalTo(DateUtil.newDateTime(2000, 1, 1, new Time(0, 0)))));
        assertThat(ttVaccineArgumentCaptor.getValue().getDosage().getDosage(), is(equalTo(TT1.getDosage())));
        assertThat(ttVaccineArgumentCaptor.getValue().getPatient().getMRSPatientId(), is(equalTo("mrsPatientId")));
    }

    private TTVisitForm setupTTVisitForm(Date vaccinationDate, TTVaccineDosage dosage) {
        final TTVisitForm ttVisitForm = new TTVisitForm();
        ttVisitForm.setFacilityId("FacilityId");
        ttVisitForm.setMotechId("PatientId");
        ttVisitForm.setDate(vaccinationDate);
        ttVisitForm.setTtDose(String.valueOf(dosage.getDosage()));
        return ttVisitForm;
    }
}
