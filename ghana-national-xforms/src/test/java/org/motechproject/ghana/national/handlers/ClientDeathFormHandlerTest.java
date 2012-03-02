package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClientDeathFormHandlerTest {

    private ClientDeathFormHandler clientDeathFormHandler;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private AllSchedules mockAllSchedules;
    @Mock
    private AllAppointments mockAllAppointments;

    @Before
    public void setUp() {
        initMocks(this);
        clientDeathFormHandler = new ClientDeathFormHandler();
        ReflectionTestUtils.setField(clientDeathFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(clientDeathFormHandler, "allSchedules", mockAllSchedules);
        ReflectionTestUtils.setField(clientDeathFormHandler, "allAppointments", mockAllAppointments);
    }

    @Test
    public void shouldDeceaseThePatient() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        Date deathDate = DateUtil.now().toDate();
        String patientMotechId = "motechId";
        String patientMRSId = "patientMRSId";
        String comment = null;
        String causeOfDeath = "NONE";
        List<String> schedules= Arrays.asList("schedule1","schedule2");
        Patient mockPatient=mock(Patient.class);

        parameters.put("formBean", clientForm(deathDate, causeOfDeath, comment));
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.clientDeath", parameters);
        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(mockPatient);
        when(mockPatient.getMRSPatientId()).thenReturn(patientMRSId);
        when(mockPatient.allCareProgramsToUnEnroll()).thenReturn(schedules);

        clientDeathFormHandler.handleFormEvent(event);

        verify(mockAllSchedules).unEnroll(patientMRSId,schedules);
        verify(mockPatientService).deceasePatient(deathDate, patientMotechId, causeOfDeath, comment);
        verify(mockAllAppointments).remove(mockPatient);
    }

    private ClientDeathForm clientForm(Date deathDate, String causeOfDeath, String comment) {
        ClientDeathForm clientDeathForm = new ClientDeathForm();
        clientDeathForm.setDate(deathDate);
        clientDeathForm.setFacilityId("facilityId");
        clientDeathForm.setMotechId("motechId");
        clientDeathForm.setStaffId("staffId");
        clientDeathForm.setCauseOfDeath(causeOfDeath);
        clientDeathForm.setComment(comment);
        return clientDeathForm;
    }

}
