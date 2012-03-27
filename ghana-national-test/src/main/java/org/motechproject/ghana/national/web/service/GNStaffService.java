package org.motechproject.ghana.national.web.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.web.util.Util;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GNStaffService {

    @Autowired
    private StaffService staffService;

    @LoginAsAdmin
    @ApiSession
    public String createStaff(String firstName, int patientCounterValue) throws UserAlreadyExistsException {
        final String roleOfStaff = StaffType.Role.COMMUNITY_HEALTH_NURSE.key();
        MRSPerson mrsPerson = new MRSPerson().firstName(firstName).lastName("LastName")
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, Util.getPhoneNumber(patientCounterValue)))
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, roleOfStaff));
        return staffService.saveUser(new MRSUser().person(mrsPerson).securityRole(StaffType.Role.securityRoleFor(roleOfStaff))).getSystemId();
    }
}
