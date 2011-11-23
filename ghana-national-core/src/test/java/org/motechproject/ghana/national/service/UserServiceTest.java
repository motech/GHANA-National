package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.UserType;
import org.motechproject.ghana.national.repository.AllUserTypes;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.services.MRSUserAdaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {

    private UserService service;
    @Mock
    private AllUserTypes allUserTypes;
    @Mock
    private MRSUserAdaptor userAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        service = new UserService(allUserTypes, userAdaptor);
    }

    @Test
    public void shouldFetchAllRoles() {
        List<UserType> userTypes = Arrays.asList(new UserType("name", "desc"));
        when(allUserTypes.getAll()).thenReturn(userTypes);

        Map<String,String> roles = service.fetchAllRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.containsKey("name"));
        assertTrue(roles.containsValue("desc"));
    }

    @Test
    public void shouldSaveUser() throws UserAlreadyExistsException {
        User user = new User();
        service.saveUser(user);
        verify(userAdaptor).saveUser(user);
    }

    @Test
    public void shouldChangePasswordGivenEmailId() throws UserAlreadyExistsException {
        final String emailId = "a@a.com";
        service.changePasswordByEmailId(emailId);
        verify(userAdaptor).setNewPasswordForUser(emailId);
    }

    @Test
    public void shouldGetAllUsers(){
        service.getAllUsers();
        verify(userAdaptor).getAllUsers();
    }


}
