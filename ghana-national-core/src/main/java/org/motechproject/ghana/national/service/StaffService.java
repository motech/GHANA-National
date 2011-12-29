package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.repository.AllStaffTypes;
import org.motechproject.ghana.national.repository.AllStaffs;
import org.motechproject.ghana.national.tools.StartsWithMatcher;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

@Service
public class StaffService {
    @Autowired
    private MRSUserAdaptor userAdaptor;
    @Autowired
    private AllStaffTypes allStaffTypes;
    @Autowired
    private AllStaffs allStaffs;

    public StaffService() {
    }

    public StaffService(AllStaffTypes allStaffTypes, MRSUserAdaptor userAdaptor, AllStaffs allStaffs) {
        this.allStaffTypes = allStaffTypes;
        this.userAdaptor = userAdaptor;
        this.allStaffs = allStaffs;
    }

    public Map saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        return userAdaptor.saveUser(mrsUser);
    }

    public Map updateUser(MRSUser mrsUser) {
        return userAdaptor.updateUser(mrsUser);
    }

    public String changePasswordByEmailId(String emailId) {
        String password = "";
        try {
            password = userAdaptor.setNewPasswordForUser(emailId);
        } catch (Exception e) {
            password = "";
        }
        return password;
    }

    public Map<String, String> fetchAllRoles() {
        Map<String, String> roles = new LinkedHashMap<String, String>();
        for (StaffType staffType : allStaffTypes.getAll()) {
            roles.put(staffType.name(), staffType.description());
        }
        return roles;
    }

    public List<MRSUser> getAllUsers() {
        return userAdaptor.getAllUsers();
    }

    public MRSUser getUserById(String userId) {
        return userAdaptor.getUserByUserName(userId);
    }

    public List<MRSUser> searchStaff(String systemId, String firstName, String middleName, String lastName, String phoneNumber, String role) {
        return allStaffs.searchStaff(systemId, firstName, middleName, lastName, phoneNumber, role, getAllUsers());
    }
}
