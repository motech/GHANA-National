package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllOutPatientVisitsTest extends BaseIntegrationTest {
    @Autowired
    AllOutPatientVisits allOutPatientVisits;

    @Test
    public void shouldAddANewOutPatientVisit(){

        OutPatientVisit visit=new OutPatientVisit();
        Date dateOfBirth = new Date(2010, 10, 10);
        visit.setDateOfBirth(dateOfBirth);

        allOutPatientVisits.add(visit);

        List<OutPatientVisit> all=allOutPatientVisits.getAll();
        assertEquals(1,all.size());

        assertEquals(dateOfBirth,all.get(0).getDateOfBirth());

    }

    @After
    public void tearDown() {
        allOutPatientVisits.removeAll();
    }
}
