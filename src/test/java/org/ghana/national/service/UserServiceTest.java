package org.ghana.national.service;

import org.ghana.national.domain.UserType;
import org.ghana.national.repository.AllUserTypes;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.services.MRSUserAdaptor;

import java.util.Arrays;
import java.util.List;

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

        List<String> roles = service.fetchAllRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.contains("name"));
    }

    @Test
    public void shouldSaveUser() throws UserAlreadyExistsException {
        User user = new User();
        service.saveUser(user);
        verify(userAdaptor).saveUser(user);
    }
}
