package org.motechproject.ghana.national.web.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.ghana.national.web.util.Util;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.patient.UnallowedIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GNPatientService {

    @Autowired
    private org.motechproject.ghana.national.service.PatientService patientService;

    @Autowired
    private CareService careService;

    public Patient createPatient(String firstName, String lastName, Date dob, String facilityId, String staffMotechId, int patientCounterValue) throws UnallowedIdentifierException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        final boolean hasBeenInsured = false;
        Boolean isDateOfBirthEstimated = true;
        final String gender = "F";

        List<Attribute> attributes = new ArrayList<Attribute>();
        setAttribute(attributes, String.valueOf(hasBeenInsured), PatientAttributes.INSURED);

        setAttribute(attributes, Util.getPhoneNumber(patientCounterValue), PatientAttributes.PHONE_NUMBER);

        MRSPerson mrsPerson = new MRSPerson().firstName(firstName).middleName("middleName")
                .lastName(lastName).dateOfBirth(dob).birthDateEstimated(isDateOfBirthEstimated)
                .gender(gender).attributes(attributes);

        return patientService.registerPatient(new Patient(new MRSPatient(null, mrsPerson, new MRSFacility(facilityId))), staffMotechId);
    }

    private void setAttribute(List<Attribute> attributes, String attributeValue, PatientAttributes patientAttribute) {
        if (StringUtils.isNotEmpty(attributeValue)) {
            attributes.add(new Attribute(patientAttribute.getAttribute(), attributeValue));
        }
    }


    public List<MRSPatient> getPatients(String firstName, String lastName, String phoneNumber, Date dateOfBirth, String insuranceNumber) {
        return patientService.getPatients(firstName, lastName, phoneNumber, dateOfBirth, insuranceNumber);
    }

    public Patient patientByOpenmrsId(String patientId) {
        return patientService.patientByOpenmrsId(patientId);
    }

    public Patient getPatientByMotechId(String patientId) {
        return patientService.getPatientByMotechId(patientId);
    }

    public void enrollForANCWithoutHistory(String staffMotechId, String facilityMrsId, String patientMotechId, Date regDate, String serialNumber, Date edd){
        careService.enroll(new ANCVO(staffMotechId, facilityMrsId, patientMotechId, regDate,
                    RegistrationToday.TODAY, serialNumber, edd, 155.0, 1, 1, Boolean.FALSE, null, null, null, null, null, null, null));
    }

    public void enrollForCWCWithoutHistory(String staffMotechId, String facilityMrsId, String patientMotechId, Date regDate, String serialNumber){
        careService.enroll(new CwcVO(staffMotechId, facilityMrsId, regDate, patientMotechId,
                    new ArrayList<CwcCareHistory>(), null, null, null, null , null, null, null, null, null, null, serialNumber, Boolean.FALSE));
    }
}
