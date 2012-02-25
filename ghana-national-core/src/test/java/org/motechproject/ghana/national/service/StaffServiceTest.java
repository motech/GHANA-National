package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.repository.AllStaffs;
import org.motechproject.ghana.national.repository.EmailGateway;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class StaffServiceTest {

    private StaffService service;
    @Mock
    private AllStaffs mockAllStaffs;
    @Mock
    private EmailGateway mockEmailGateway;

    @Before
    public void setUp() {
        initMocks(this);
        service = new StaffService(mockAllStaffs, mockEmailGateway);
    }

    @Test
    public void shouldFetchAllRoles() {
        service.fetchAllRoles();
        verify(mockAllStaffs).fetchAllRoles();
    }

    @Test
    public void shouldSaveUser() throws UserAlreadyExistsException {
        MRSUser mrsUser = new MRSUser();
        service.saveUser(mrsUser);
        verify(mockAllStaffs).saveUser(mrsUser);
    }

    @Test
    public void shouldEmailIfPasswordGetsChangedSuccessfully() {
        String emailId = "a@a.com";
        String changedPassword = "changedPassword";
        when(mockAllStaffs.changePasswordByEmailId(emailId)).thenReturn(changedPassword);
        
        service.changePasswordByEmailId(emailId);
        
        verify(mockEmailGateway).sendEmailUsingTemplates(emailId, changedPassword);
    }
    
    @Test
    public void shouldReturnAsUserNotFoundIfPasswordIsNotChanged() {
        String emailId = "a@a.com";
        when(mockAllStaffs.changePasswordByEmailId(emailId)).thenReturn("");

        int result = service.changePasswordByEmailId(emailId);

        verify(mockEmailGateway, never()).sendEmailUsingTemplates(anyString(), anyString());
        assertThat(result, is(Constants.EMAIL_USER_NOT_FOUND));
    }

    @Test
    public void shouldGetAllUsers() {
        service.getAllUsers();
        verify(mockAllStaffs).getAllUsers();
    }

    @Test
    public void shouldGetUserById() {
        String userId = "124567";
        service.getUserByEmailIdOrMotechId(userId);
        verify(mockAllStaffs).getUserByEmailIdOrMotechId(userId);
    }

    @Test
    public void shouldSearchStaff() {
        String firstName1 = "Khaarthi";
        String firstName2 = "gha";
        String middileName = "mm";

        MRSUser mrsUser1 = new MRSUser().person(new MRSPerson().firstName(firstName1).middleName(middileName));
        MRSUser mrsUser2 = new MRSUser().person(new MRSPerson().firstName(firstName2).middleName(middileName));
        List<MRSUser> expectedMRSUsers = Arrays.asList(mrsUser1, mrsUser2);

        when(mockAllStaffs.getAllUsers()).thenReturn(expectedMRSUsers);

        service.searchStaff(null, null, middileName, null, null, null);

        verify(mockAllStaffs).searchStaff(null, null, middileName, null, null, null);
    }
}
