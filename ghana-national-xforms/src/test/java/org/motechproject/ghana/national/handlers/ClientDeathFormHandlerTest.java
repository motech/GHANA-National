package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClientDeathFormHandlerTest {

    private ClientDeathFormHandler clientDeathFormHandler;
    @Mock
    private PatientService mockPatientService;

    @Before
    public void setUp() {
        initMocks(this);
        clientDeathFormHandler = new ClientDeathFormHandler();
        ReflectionTestUtils.setField(clientDeathFormHandler, "patientService", mockPatientService);
    }

    @Test
    public void shouldDeceaseThePatient() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        Date deathDate = DateUtil.now().toDate();
        String comment = null;
        String causeOfDeath = "NONE";
        parameters.put("formBean", clientForm(deathDate, causeOfDeath, comment));
        MotechEvent event = new MotechEvent("form.validation.successful.NurseDataEntry.clientDeath", parameters);

        clientDeathFormHandler.handleFormEvent(event);
        verify(mockPatientService).deceasePatient(deathDate, "motechId", causeOfDeath, comment);
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
