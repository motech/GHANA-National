package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildVisitServiceTest extends BaseUnitTest {
    private ChildVisitService service;
    @Mock
    AllEncounters allEncounters;

    @Before
    public void setUp() {
        service = new ChildVisitService();
        initMocks(this);
        ReflectionTestUtils.setField(service, "allEncounters", allEncounters);
    }

    @Test
    public void shouldCreateEncounterForCWCVisitWithAllInfo() {
        MRSUser staff = mock(MRSUser.class);
        Facility facility = mock(Facility.class);
        Patient patient = mock(Patient.class);
        CWCVisit cwcVisit = createTestCWCVisit(new Date(), staff, facility, patient);

        service.save(cwcVisit);

        ArgumentCaptor<Encounter> encounterCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(allEncounters).persistEncounter(encounterCaptor.capture());
    }

    private CWCVisit createTestCWCVisit(Date registrationDate, MRSUser staff, Facility facility, Patient patient) {
        CWCVisit cwcVisit = new CWCVisit();
        return cwcVisit.staff(staff).facility(facility).patient(patient).date(registrationDate).serialNumber("4ds65")
                .weight(65.67d).comments("comments").cwcLocation("34").house("house").community("community").maleInvolved(false);
    }

}
