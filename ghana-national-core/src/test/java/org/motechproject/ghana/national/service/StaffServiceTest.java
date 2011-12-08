package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.repository.AllStaffTypes;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSUserAdaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StaffServiceTest {

    private StaffService service;
    @Mock
    private AllStaffTypes allStaffTypes;
    @Mock
    private MRSUserAdaptor userAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        service = new StaffService(allStaffTypes, userAdaptor);
    }

    @Test
    public void shouldFetchAllRoles() {
        List<StaffType> staffTypes = Arrays.asList(new StaffType("name", "desc"));
        when(allStaffTypes.getAll()).thenReturn(staffTypes);

        Map<String, String> roles = service.fetchAllRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.containsKey("name"));
        assertTrue(roles.containsValue("desc"));
    }

    @Test
    public void shouldSaveUser() throws UserAlreadyExistsException {
        MRSUser mrsUser = new MRSUser();
        service.saveUser(mrsUser);
        verify(userAdaptor).saveUser(mrsUser);
    }

    @Test
    public void shouldChangePasswordGivenEmailId() throws UserAlreadyExistsException {
        final String emailId = "a@a.com";
        service.changePasswordByEmailId(emailId);
        verify(userAdaptor).setNewPasswordForUser(emailId);
    }

    @Test
    public void shouldGetAllUsers() {
        service.getAllUsers();
        verify(userAdaptor).getAllUsers();
    }

    @Test
    public void shouldGetUserById() {
        String userId = "124567";
        MRSUser mrsUser = new MRSUser();
        when(userAdaptor.getUserBySystemId(userId)).thenReturn(mrsUser);
        assertThat(service.getUserById(userId), is(equalTo(mrsUser)));
    }

    @Test
    public void shouldSearchForUsersWithRespectiveSearchCriteria() {
        final String middleName = "middleName";
        final MRSUser user1 = new MRSUser().systemId("123").firstName("firstName").middleName(middleName);
        user1.setAttributes(Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, "0123456789")));
        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().firstName("first").systemId("1234"));
        when(userAdaptor.getAllUsers()).thenReturn(expectedMRSUsers);
        final List<MRSUser> actualMRSUsers = service.searchStaff("123", "first", middleName, "", "01234", "");
        assertEquals(1, actualMRSUsers.size());
    }

    @Test
    public void shouldGiveSearchResultsSortedByFirstName() {
        String firstName1 = "Khaarthi";
        String firstName2 = "gha";
        String middileName = "mm";
        List<MRSUser> expectedMRSUsers = Arrays.asList(new MRSUser().firstName(firstName1).middleName(middileName), new MRSUser().firstName(firstName2).middleName(middileName));
        when(userAdaptor.getAllUsers()).thenReturn(expectedMRSUsers);
        final List<MRSUser> actualMRSUsers = service.searchStaff(null, null, middileName, null, null, null);
        assertEquals(2, actualMRSUsers.size());
        assertEquals(firstName2, actualMRSUsers.get(0).getFirstName());
        assertEquals(firstName1, actualMRSUsers.get(1).getFirstName());
    }

    @Test
    public void shouldSearchForUsersWithRespectiveRoles() {
        final MRSUser user1 = new MRSUser().id("123").firstName("firstName").middleName("middleName");
        user1.setAttributes(Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, "admin")));
        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().firstName("firstNm").id("1234"));
        when(userAdaptor.getAllUsers()).thenReturn(expectedMRSUsers);
        final List<MRSUser> actualMRSUsers = service.searchStaff("", "", "", "", "", "admin");
        assertEquals(1, actualMRSUsers.size());
    }

    @Test
    public void shouldReturnEmptyIfSearchDoesntReturnAnyResults() {
        final List<MRSUser> expectedMRSUsers = Arrays.asList(new MRSUser().firstName("firstNm").id("1234"));
        when(userAdaptor.getAllUsers()).thenReturn(expectedMRSUsers);
        final List<MRSUser> actualMRSUsers = service.searchStaff("123", "first", "middle", "", "01234", "");
        assertEquals(0, actualMRSUsers.size());
    }
}
