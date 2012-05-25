package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.ClientDeathForm;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class ClientDeathFormHandlerTest {

    private ClientDeathFormHandler clientDeathFormHandler;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private MobileMidwifeService mockMobileMidwifeService;

    @Before
    public void setUp() {
        initMocks(this);
        clientDeathFormHandler = new ClientDeathFormHandler();
        setField(clientDeathFormHandler, "patientService", mockPatientService);
        setField(clientDeathFormHandler, "mobileMidwifeService", mockMobileMidwifeService);
    }

    @Test
    public void shouldRethrowException() throws PatientNotFoundException {
        doThrow(new RuntimeException()).when(mockPatientService).deceasePatient(Matchers.<Date>any(), anyString(), anyString(), anyString());
        try {
            clientDeathFormHandler.handleFormEvent(new ClientDeathForm());
            fail("Should handle exception");
        } catch (XFormHandlerException e) {
            assertThat(e.getMessage(), is("Encountered error while processing client death form"));
        }
    }

    @Test
    public void shouldDeceaseThePatient() throws PatientNotFoundException {
        Date deathDate = DateUtil.now().toDate();
        String patientMotechId = "motechId";
        String patientMRSId = "patientMRSId";
        String comment = null;
        String causeOfDeath = "NONE";
        Patient mockPatient = mock(Patient.class);

        when(mockPatient.getMRSPatientId()).thenReturn(patientMRSId);

        clientDeathFormHandler.handleFormEvent(clientForm(deathDate,causeOfDeath,comment));

        verify(mockPatientService).deceasePatient(deathDate, patientMotechId, causeOfDeath, comment);
        verify(mockMobileMidwifeService).unRegister(patientMotechId);
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
