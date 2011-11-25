package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.UserType;
import org.motechproject.ghana.national.repository.AllUserTypes;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private MRSUserAdaptor userAdaptor;
    @Autowired
    private AllUserTypes allUserTypes;

    public UserService() {
    }

    public UserService(AllUserTypes allUserTypes, MRSUserAdaptor userAdaptor) {
        this.allUserTypes = allUserTypes;
        this.userAdaptor = userAdaptor;
    }

    public Map saveUser(User user) throws UserAlreadyExistsException {
        return userAdaptor.saveUser(user);
    }

    public String changePasswordByEmailId(String emailId){
        String password ="";
        try{
            password = userAdaptor.setNewPasswordForUser(emailId);
        }catch(Exception e){
            password = "";
        }
        return password;
    }

    public Map<String,String> fetchAllRoles() {
        Map<String,String> roles = new HashMap<String,String>();
        for (UserType userType : allUserTypes.getAll()) roles.put(userType.name(),userType.description());
        return roles;
    }

    public List<User> getAllUsers() {
        return userAdaptor.getAllUsers();
    }

    public User getUserById(String userId) {
        return userAdaptor.getUserById(userId);
    }
}
