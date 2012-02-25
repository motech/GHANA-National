package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.AllStaffs;
import org.motechproject.ghana.national.repository.EmailGateway;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StaffService {
    @Autowired
    private AllStaffs allStaffs;

    @Autowired
    private EmailGateway emailGateway;

    public StaffService() {
    }

    public StaffService(AllStaffs allStaffs, EmailGateway emailGateway) {
        this.allStaffs = allStaffs;
        this.emailGateway = emailGateway;
    }

    public Map saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        return allStaffs.saveUser(mrsUser);
    }

    public Map updateUser(MRSUser mrsUser) {
        return allStaffs.updateUser(mrsUser);
    }

    public int changePasswordByEmailId(String emailId) {
        String newPassword = allStaffs.changePasswordByEmailId(emailId);
        if (!newPassword.equals("")) {
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
