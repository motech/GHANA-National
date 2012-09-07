package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.repository.AllStaffs;
import org.motechproject.ghana.national.repository.EmailGateway;
import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.services.OpenMRSUserAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StaffService {
    private AllStaffs allStaffs;
    private EmailGateway emailGateway;
    private IdentifierGenerator identifierGenerator;

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffService.class);

    @Autowired
    public StaffService(AllStaffs allStaffs, EmailGateway emailGateway, IdentifierGenerator identifierGenerator) {
        this.allStaffs = allStaffs;
        this.emailGateway = emailGateway;
        this.identifierGenerator = identifierGenerator;
    }

    public MRSUser saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        mrsUser.systemId(identifierGenerator.newStaffId());
        Map userData = allStaffs.saveUser(mrsUser);
        final MRSUser openMRSUser = (MRSUser) userData.get(OpenMRSUserAdapter.USER_KEY);
        if (StaffType.Role.isAdmin(openMRSUser.getPerson().attrValue(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE))) {
            emailGateway.sendEmailUsingTemplates(openMRSUser.getUserName(), (String) userData.get(OpenMRSUserAdapter.PASSWORD_USER_KEY));
        }
        return openMRSUser;
    }

    public Map updateUser(MRSUser mrsUser) {
        return allStaffs.updateUser(mrsUser);
    }

    public int changePasswordByEmailId(String emailId) {
        String newPassword = allStaffs.changePasswordByEmailId(emailId);
        if (!newPassword.equals("")) {
            LOGGER.info("User Id:\t" + emailId + "\tPassword:\t" + newPassword);
            return emailGateway.sendEmailUsingTemplates(emailId, newPassword);
        }
        return Constants.EMAIL_USER_NOT_FOUND;
    }

    public Map<String, String> fetchAllRoles() {
        return allStaffs.fetchAllRoles();
    }

    public List<MRSUser> getAllUsers() {
        return allStaffs.getAllUsers();
    }

    public MRSUser getUserByEmailIdOrMotechId(String userId) {
        return allStaffs.getUserByEmailIdOrMotechId(userId);
    }

    public List<MRSUser> searchStaff(String systemId, String firstName, String middleName, String lastName, String phoneNumber, String role) {
        return allStaffs.searchStaff(systemId, firstName, middleName, lastName, phoneNumber, role);
    }
}
