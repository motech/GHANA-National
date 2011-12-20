package org.motechproject.ghana.national.web.helper;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.openmrs.patient.UnallowedIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.safeToString;

public class PatientHelper {

    @Autowired
    private MotechIdVerhoeffValidator motechIdVerhoeffValidator;

    public Patient getPatientVO(PatientForm createPatientForm, String patientID) throws UnallowedIdentifierException {
        List<Attribute> attributes = Arrays.asList(
                new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), safeToString(createPatientForm.getPhoneNumber())),
                new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute(), safeToString(createPatientForm.getNhisExpirationDate())),
                new Attribute(PatientAttributes.NHIS_NUMBER.getAttribute(), createPatientForm.getNhisNumber()),
                new Attribute(PatientAttributes.INSURED.getAttribute(), safeToString(createPatientForm.getInsured())));
        if (patientID.equals("")) {
            if (!motechIdVerhoeffValidator.isValid(createPatientForm.getMotechId()))
                throw new UnallowedIdentifierException("User Id is not allowed");
            patientID = createPatientForm.getMotechId();
        }

        MRSPerson mrsPerson = new MRSPerson().firstName(createPatientForm.getFirstName()).middleName(createPatientForm.getMiddleName())
                .lastName(createPatientForm.getLastName()).preferredName(createPatientForm.getPreferredName()).dateOfBirth(createPatientForm.getDateOfBirth())
                .birthDateEstimated(createPatientForm.getEstimatedDateOfBirth()).gender(createPatientForm.getSex()).address(createPatientForm.getAddress()).attributes(attributes);
        MRSPatient mrsPatient = new MRSPatient(patientID, mrsPerson, new MRSFacility(createPatientForm.getFacilityId()));

        return new Patient(mrsPatient);
    }
}
