package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.repository.AllStaffs;
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

    public StaffService() {
    }

    public StaffService(AllStaffs allStaffs) {
        this.allStaffs = allStaffs;
    }

    public Map saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        return allStaffs.saveUser(mrsUser);
    }

    public Map updateUser(MRSUser mrsUser) {
        return allStaffs.updateUser(mrsUser);
    }

    public String changePasswordByEmailId(String emailId) {
        return allStaffs.changePasswordByEmailId(emailId);
    }

    public Map<String, String> fetchAllRoles() {
        return allStaffs.fetchAllRoles();
    }

    public List<MRSUser> getAllUsers() {
        return allStaffs.getAllUsers();
    }

    public MRSUser getUserById(String userId) {
        return allStaffs.getUserById(userId);
    }

    public List<MRSUser> searchStaff(String systemId, String firstName, String middleName, String lastName, String phoneNumber, String role) {
        return allStaffs.searchStaff(systemId, firstName, middleName, lastName, phoneNumber, role);
    }
}
