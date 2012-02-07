package org.motechproject.ghana.national.service;


import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.repository.AllOutPatientVisits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OutPatientVisitService {

    @Autowired
    AllOutPatientVisits allOutPatientVisits;


    public void registerVisit(OutPatientVisit visit){
        allOutPatientVisits.add(visit);
    }

}
