package org.ghana.national.service;

import org.ghana.national.domain.UserType;
import org.ghana.national.exception.UserAlreadyFoundException;
import org.ghana.national.repository.AllUserTypes;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private MRSUserAdaptor userAdaptor;
    private AllUserTypes allUserTypes;

    @Autowired
    public UserService(AllUserTypes allUserTypes, MRSUserAdaptor userAdaptor) {
        this.allUserTypes = allUserTypes;
        this.userAdaptor = userAdaptor;
    }

    public String saveUser(User user) throws UserAlreadyFoundException {
        return userAdaptor.saveUser(user);
    }

    public List<String> fetchAllRoles() {
        List<String> roles = new ArrayList<String>();
        for (UserType userType : allUserTypes.getAll()) roles.add(userType.name());
        return roles;
    }
}
