package org.motechproject.ghana.national.tools;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.*;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.openmrs.patient.UnallowedIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TestDataGenerator {

    @Autowired
    private org.motechproject.ghana.national.service.PatientService patientService;

    @Autowired
    private CareService careService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private StaffService staffService;

    @LoginAsAdmin
    @ApiSession
    public List<Facility> getAFacility() {
        return facilityService.searchFacilities(null, "Ashanti", "Ghana", "Ashanti", null, null);
    }

    @LoginAsAdmin
    @ApiSession
    public String createStaff(String firstName, String phoneNumber) throws UserAlreadyExistsException {
        final String roleOfStaff = StaffType.Role.COMMUNITY_HEALTH_NURSE.key();
        MRSPerson mrsPerson = new MRSPerson().firstName(firstName).lastName("LastName")
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, phoneNumber))
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, roleOfStaff));
        return staffService.saveUser(new MRSUser().person(mrsPerson).securityRole(StaffType.Role.securityRoleFor(roleOfStaff))).getSystemId();
    }

    @LoginAsAdmin
    @ApiSession
    public Patient createPatient(String firstName, String lastName, Date dob, String facilityId, String staffMotechId, String phoneNumber) throws UnallowedIdentifierException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {
        final boolean hasBeenInsured = false;
        Boolean isDateOfBirthEstimated = true;
        final String gender = "F";

        List<Attribute> attributes = new ArrayList<Attribute>();
        setAttribute(attributes, String.valueOf(hasBeenInsured), PatientAttributes.INSURED);

        setAttribute(attributes, phoneNumber, PatientAttributes.PHONE_NUMBER);

        MRSPerson mrsPerson = new MRSPerson().firstName(firstName).middleName("middleName")
                .lastName(lastName).dateOfBirth(dob).birthDateEstimated(isDateOfBirthEstimated)
                .gender(gender).attributes(attributes);

        return patientService.registerPatient(new Patient(new MRSPatient(null, mrsPerson, new MRSFacility(facilityId))), staffMotechId, new Date());
    }

    private void setAttribute(List<Attribute> attributes, String attributeValue, PatientAttributes patientAttribute) {
        if (StringUtils.isNotEmpty(attributeValue)) {
            attributes.add(new Attribute(patientAttribute.getAttribute(), attributeValue));
        }
    }
}