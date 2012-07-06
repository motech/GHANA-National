package org.motechproject.ghana.national.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.tools.StartsWithMatcher;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Repository
public class AllStaffs {

    private AllStaffTypes allStaffTypes;
    //todo remove dependency on userAdapter - move to service layer - this class is just an abstraction of couch repo
    private MRSUserAdapter userAdapter;

    @Autowired
    public AllStaffs(AllStaffTypes allStaffTypes, MRSUserAdapter userAdapter) {
        this.allStaffTypes = allStaffTypes;
        this.userAdapter = userAdapter;
    }

    public List<MRSUser> searchStaff(String systemId, String firstName, String middleName, String lastName, String phoneNumber, String role) {
        List<MRSUser> allUsers = getAllUsers();
        allUsers = filterUsers(on(MRSUser.class).getSystemId(), systemId, allUsers);
        allUsers = filterUsers(on(MRSUser.class).getPerson().getFirstName(), firstName, allUsers);
        allUsers = filterUsers(on(MRSUser.class).getPerson().getMiddleName(), middleName, allUsers);
        allUsers = filterUsers(on(MRSUser.class).getPerson().getLastName(), lastName, allUsers);
        allUsers = filterByPhone(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, phoneNumber, allUsers);
        allUsers = filterByRole(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role, allUsers);
        Collections.sort(allUsers, new UserFirstNameComparator());
        return allUsers;
    }

    private List<MRSUser> filterUsers(String field, String matcher, List<MRSUser> filteredMRSUsers) {
        return (StringUtils.isNotEmpty(matcher)) ? filter(having(field, StartsWithMatcher.ignoreCaseStartsWith(matcher)), filteredMRSUsers) : filteredMRSUsers;
    }


    private List<MRSUser> filterByRole(String searchAttribute, String searchCriteria, List<MRSUser> filteredMRSUsers) {
        if (StringUtils.isEmpty(searchCriteria)) {
            return filteredMRSUsers;
        }
        ArrayList<MRSUser> filteredList = new ArrayList<MRSUser>();
        for (MRSUser filteredMRSUser : filteredMRSUsers) {
            List<Attribute> attributeSearchResults = filter(having(on(Attribute.class).name(), equalTo(searchAttribute)), filteredMRSUser.getPerson().getAttributes());
            if (CollectionUtils.isNotEmpty(attributeSearchResults) && attributeSearchResults.get(0).value().trim().equalsIgnoreCase(searchCriteria)) {
                filteredList.add(filteredMRSUser);
            }
        }
        return filteredList;
    }

    private List<MRSUser> filterByPhone(String searchAttribute, String searchCriteria, List<MRSUser> filteredMRSUsers) {
        ArrayList<MRSUser> filteredList = new ArrayList<MRSUser>();
        for (MRSUser filteredMRSUser : filteredMRSUsers) {
            List<Attribute> attributeSearchResults = filter(having(on(Attribute.class).name(), equalTo(searchAttribute)), filteredMRSUser.getPerson().getAttributes());
            if (CollectionUtils.isNotEmpty(attributeSearchResults) && attributeSearchResults.get(0).value().startsWith(searchCriteria)) {
                filteredList.add(filteredMRSUser);
            }
        }
        return (CollectionUtils.isEmpty(filteredList)) ? filteredMRSUsers : filteredList;
    }

    public Map<String, String> fetchAllRoles() {
        Map<String, String> roles = new LinkedHashMap<String, String>();
        for (StaffType staffType : allStaffTypes.getAll()) {
            roles.put(staffType.getName(), staffType.getDescription());
        }
        return roles;
    }

    public Map saveUser(MRSUser mrsUser) throws UserAlreadyExistsException {
        return userAdapter.saveUser(mrsUser);
    }

    public Map updateUser(MRSUser mrsUser) {
        return userAdapter.updateUser(mrsUser);
    }

    public String changePasswordByEmailId(String emailId) {
        String password = "";
        try {
            password = userAdapter.setNewPasswordForUser(emailId);
        } catch (Exception e) {
            password = "";
        }
        return password;
    }

    public List<MRSUser> getAllUsers() {
        return userAdapter.getAllUsers();
    }

    public MRSUser getUserByEmailIdOrMotechId(String userId) {
        return userAdapter.getUserByUserName(userId);
    }

    private class UserFirstNameComparator implements Comparator<MRSUser> {
        @Override
        public int compare(MRSUser user1, MRSUser user2) {
            return user2.getPerson().getFirstName().compareTo(user1.getPerson().getFirstName());
        }
    }
}
