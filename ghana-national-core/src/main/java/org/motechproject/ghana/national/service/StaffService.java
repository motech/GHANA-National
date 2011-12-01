package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.repository.AllStaffTypes;
import org.motechproject.ghana.national.tools.StartsWithMatcher;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Service
public class StaffService {
    @Autowired
    private MRSUserAdaptor userAdaptor;
    @Autowired
    private AllStaffTypes allStaffTypes;

    public StaffService() {
    }

    public StaffService(AllStaffTypes allStaffTypes, MRSUserAdaptor userAdaptor) {
        this.allStaffTypes = allStaffTypes;
        this.userAdaptor = userAdaptor;
    }

    public Map saveUser(User user) throws UserAlreadyExistsException {
        return userAdaptor.saveUser(user);
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
        Map<String, String> roles = new HashMap<String, String>();
        for (StaffType staffType : allStaffTypes.getAll()) roles.put(staffType.name(), staffType.description());
        return roles;
    }

    public List<User> getAllUsers() {
        return userAdaptor.getAllUsers();
    }

    public User getUserById(String userId) {
        return userAdaptor.getUserById(userId);
    }

    public List<User> searchStaff(String id, String firstName, String middleName, String lastName, String phoneNumber, String role) {
        List<User> filteredUsers = getAllUsers();
        filteredUsers = filterUsers(on(User.class).getId(), id, filteredUsers);
        filteredUsers = filterUsers(on(User.class).getFirstName(), firstName, filteredUsers);
        filteredUsers = filterUsers(on(User.class).getMiddleName(), middleName, filteredUsers);
        filteredUsers = filterUsers(on(User.class).getLastName(), lastName, filteredUsers);
        filteredUsers = filterByAttribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, phoneNumber, filteredUsers);
        filteredUsers = filterByAttribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role, filteredUsers);
        Collections.sort(filteredUsers,new UserFirstNameComparator());
        return filteredUsers;
    }

    private List<User> filterByAttribute(String searchAttribute, String searchCriteria, List<User> filteredUsers) {
        final ArrayList<User> filteredList = new ArrayList<User>();
        for (User filteredUser : filteredUsers) {
            final List<Attribute> attributeSearchResults = filter(having(on(Attribute.class).name(), equalTo(searchAttribute)), filteredUser.getAttributes());
            if (CollectionUtils.isNotEmpty(attributeSearchResults) && attributeSearchResults.get(0).value().startsWith(searchCriteria)) {
                filteredList.add(filteredUser);
            }
        }
        return (CollectionUtils.isEmpty(filteredList)) ? filteredUsers : filteredList;
    }

    private List<User> filterUsers(String field, String matcher, List<User> filteredUsers) {
        return (StringUtils.isNotEmpty(matcher)) ? filter(having(field, StartsWithMatcher.ignoreCaseStartsWith(matcher)), filteredUsers) : filteredUsers;
    }

    private class UserFirstNameComparator implements Comparator<User> {
        @Override
        public int compare(User user1, User user2) {
            return user2.getFirstName().compareTo(user1.getFirstName());
        }
    }
}
