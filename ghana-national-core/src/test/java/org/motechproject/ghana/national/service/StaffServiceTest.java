package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.repository.AllStaffs;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StaffServiceTest {

    private StaffService service;
    @Mock
    private AllStaffs allStaffs;

    @Before
    public void setUp() {
        initMocks(this);
        service = new StaffService(allStaffs);
    }

    @Test
    public void shouldFetchAllRoles() {
        service.fetchAllRoles();
        verify(allStaffs).fetchAllRoles();
    }

    @Test
    public void shouldSaveUser() throws UserAlreadyExistsException {
        MRSUser mrsUser = new MRSUser();
        service.saveUser(mrsUser);
        verify(allStaffs).saveUser(mrsUser);
    }

    @Test
    public void shouldChangePasswordGivenEmailId() throws UserAlreadyExistsException {
        final String emailId = "a@a.com";
        service.changePasswordByEmailId(emailId);
        verify(allStaffs).changePasswordByEmailId(emailId);
    }

    @Test
    public void shouldGetAllUsers() {
        service.getAllUsers();
        verify(allStaffs).getAllUsers();
    }

    @Test
    public void shouldGetUserById() {
        String userId = "124567";
        service.getUserByEmailIdOrMotechId(userId);
        verify(allStaffs).getUserByEmailIdOrMotechId(userId);
    }

    @Test
    public void shouldSearchStaff() {
        String firstName1 = "Khaarthi";
        String firstName2 = "gha";
        String middileName = "mm";

        MRSUser mrsUser1 = new MRSUser().person(new MRSPerson().firstName(firstName1).middleName(middileName));
        MRSUser mrsUser2 = new MRSUser().person(new MRSPerson().firstName(firstName2).middleName(middileName));
        List<MRSUser> expectedMRSUsers = Arrays.asList(mrsUser1, mrsUser2);

        when(allStaffs.getAllUsers()).thenReturn(expectedMRSUsers);

        service.searchStaff(null, null, middileName, null, null, null);

        verify(allStaffs).searchStaff(null, null, middileName, null, null, null);
    }
}
