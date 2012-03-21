package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllOutPatientVisitsTest extends BaseIntegrationTest {
    @Autowired
    AllOutPatientVisits allOutPatientVisits;
    @Mock
    AllMotechModuleOutPatientVisits mockAllMotechModuleOutPatientVisits;

    @Before
    public void init() {
        initMocks(this);
        ReflectionTestUtils.setField(allOutPatientVisits, "allMotechModuleOutPatientVisits",
                mockAllMotechModuleOutPatientVisits);
    }


    @Test
    public void shouldAddANewOutPatientVisit() {
        OutPatientVisit visit = new OutPatientVisit();
        Date dateOfBirth = new Date(2010, 10, 10);
        visit.setDateOfBirth(dateOfBirth);

        allOutPatientVisits.add(visit);

        List<OutPatientVisit> all = allOutPatientVisits.getAll();
        assertEquals(1, all.size());
        assertEquals(dateOfBirth, all.get(0).getDateOfBirth());
        verify(mockAllMotechModuleOutPatientVisits).save(visit);
    }

    @After
    public void tearDown() {
        allOutPatientVisits.removeAll();
    }
}
