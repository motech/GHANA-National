package org.motechproject.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.AllPatients;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.InvalidCheckDigitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.emptyToNull;

@Service
public class PatientService {
    AllPatients allPatients;
    public PatientService() {
    }

    @Autowired
    public PatientService(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public void registerPatient(Patient patient, PatientType typeOfPatient, String parentId)
            throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        if (PatientType.CHILD_UNDER_FIVE.equals(typeOfPatient) && StringUtils.isNotEmpty(parentId)) {
            Patient mother = allPatients.patientById(parentId);
            if (mother == null) {
                throw new ParentNotFoundException();
            }
        }
        try {
            allPatients.add(patient);
        } catch (IdentifierNotUniqueException e) {
            throw new PatientIdNotUniqueException();
        } catch (InvalidCheckDigitException e) {
            throw new PatientIdIncorrectFormatException();
        }
    }

    public Patient getPatientById(String patientId) {
        return allPatients.patientById(patientId);
    }

    public List<Patient> search(String name, String motechId) {
        return allPatients.search(emptyToNull(name), emptyToNull(motechId));
    }
}
