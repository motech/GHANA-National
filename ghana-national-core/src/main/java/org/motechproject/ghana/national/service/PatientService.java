package org.motechproject.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.AllPatients;
import org.openmrs.api.IdentifierNotUniqueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    AllPatients allPatients;

    @Autowired
    public PatientService(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public void registerPatient(Patient patient, PatientType typeOfPatient, String parentId) throws ParentNotFoundException, PatientIdNotUniqueException {
        if(PatientType.CHILD.equals(typeOfPatient) && StringUtils.isNotEmpty(parentId)) {
            Patient mother = allPatients.patientById(parentId);
            if(mother == null) {
                throw new ParentNotFoundException();
            }
        }
        try {
        allPatients.add(patient);
        } catch(IdentifierNotUniqueException e) {
            throw new PatientIdNotUniqueException();
        }
    }
}
