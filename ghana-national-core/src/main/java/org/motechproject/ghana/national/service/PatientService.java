package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    AllPatients allPatients;

    @Autowired
    public PatientService(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public void registerPatient(Patient patient, PatientType typeOfPatient, String parentId) throws ParentNotFoundException {
        if(PatientType.CHILD.equals(typeOfPatient)) {
            final Patient mother = allPatients.patientById(parentId);
            if(mother == null) {
                throw new ParentNotFoundException();
            }
        }
        allPatients.add(patient);
    }
}
