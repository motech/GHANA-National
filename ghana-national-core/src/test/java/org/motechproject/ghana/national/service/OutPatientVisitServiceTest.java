package org.motechproject.ghana.national.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.repository.AllOutPatientVisits;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutPatientVisitServiceTest {

    @Mock
    AllOutPatientVisits mockAllOutPatientVisits;

    OutPatientVisitService service;

    @Before
    public void setUp(){
        initMocks(this);
        service=new OutPatientVisitService(mockAllOutPatientVisits);
    }

    @Test
    public void shouldRegisterForOutPatientVisit(){
        OutPatientVisit visit=new OutPatientVisit();
        service.registerVisit(visit);
        verify(mockAllOutPatientVisits).add(visit);
    }

}
