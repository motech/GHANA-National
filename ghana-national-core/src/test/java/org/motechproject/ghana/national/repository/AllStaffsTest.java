package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
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
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllStaffsTest {
    private AllStaffs allStaffs;
    @Mock
    private AllStaffTypes allStaffTypes;
    @Mock
    private MRSUserAdaptor userAdaptor;

    @Before
    public void setUp() {
        initMocks(this);
        allStaffs = new AllStaffs(allStaffTypes, userAdaptor);
    }

    @Test
    public void shouldFetchAllRoles() {
        List<StaffType> staffTypes = Arrays.asList(new StaffType("name", "desc"));
        when(allStaffTypes.getAll()).thenReturn(staffTypes);

        Map<String, String> roles = allStaffs.fetchAllRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.containsKey("name"));
        assertTrue(roles.containsValue("desc"));
    }

    @Test
    public void shouldGetAllUsers() {
        allStaffs.getAllUsers();
        verify(userAdaptor).getAllUsers();
    }

    @Test
    public void shouldGetUserById() {
        String userId = "124567";
        MRSUser mrsUser = new MRSUser();
        when(userAdaptor.getUserByUserName(userId)).thenReturn(mrsUser);
        assertThat(allStaffs.getUserById(userId), is(equalTo(mrsUser)));
    }

    @Test
    public void shouldChangePasswordGivenEmailId() throws UserAlreadyExistsException {
        final String emailId = "a@a.com";
        allStaffs.changePasswordByEmailId(emailId);
        verify(userAdaptor).setNewPasswordForUser(emailId);
    }

    @Test
    public void shouldSearchForUsersWithRespectiveSearchCriteria() {
        final String middleName = "middleName";
        MRSPerson mrsPerson = new MRSPerson().firstName("firstName").middleName(middleName)
                .attributes(Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, "0123456789")));
        final MRSUser user1 = new MRSUser().systemId("123").person(mrsPerson);

        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().person(new MRSPerson().firstName("first")).systemId("1234"));
        AllStaffs spy = spy(allStaffs);
        doReturn(expectedMRSUsers).when(spy).getAllUsers();
        final List<MRSUser> actualMRSUsers = spy.searchStaff("123", "first", middleName, "", "01234", "");
        assertEquals(1, actualMRSUsers.size());
    }

    @Test
    public void shouldGiveSearchResultsSortedByFirstName() {
        String firstName1 = "Khaarthi";
        String firstName2 = "gha";
        String middileName = "mm";
        MRSUser mrsUser1 = new MRSUser().person(new MRSPerson().firstName(firstName1).middleName(middileName));
        MRSUser mrsUser2 = new MRSUser().person(new MRSPerson().firstName(firstName2).middleName(middileName));
        List<MRSUser> expectedMRSUsers = Arrays.asList(mrsUser1, mrsUser2);
        AllStaffs spy = spy(allStaffs);
        doReturn(expectedMRSUsers).when(spy).getAllUsers();
        final List<MRSUser> actualMRSUsers = spy.searchStaff(null, null, middileName, null, null, null);
        assertEquals(2, actualMRSUsers.size());
        assertEquals(firstName2, actualMRSUsers.get(0).getPerson().getFirstName());
        assertEquals(firstName1, actualMRSUsers.get(1).getPerson().getFirstName());
    }

    @Test
    public void shouldSearchForUsersWithRespectiveRoles() {
        List<Attribute> attributeList = Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, "admin"));
        final MRSUser user1 = new MRSUser().id("123").person(new MRSPerson().firstName("firstName").middleName("middleName").attributes(attributeList));
        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().person(new MRSPerson().firstName("firstNm")).id("1234"));
        AllStaffs spy = spy(allStaffs);
        doReturn(expectedMRSUsers).when(spy).getAllUsers();
        final List<MRSUser> actualMRSUsers = spy.searchStaff("", "", "", "", "", "admin");
        assertEquals(1, actualMRSUsers.size());
    }

    @Test
    public void shouldSearchForUsersWithRespectiveRolesAndReturnEmptyIfRolesAreEmpty() {
        List<Attribute> attributeList = Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, ""));
        final MRSUser user1 = new MRSUser().id("123").person(new MRSPerson().firstName("firstName").middleName("middleName").attributes(attributeList));
        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().person(new MRSPerson().firstName("firstNm")).id("1234"));
        AllStaffs spy = spy(allStaffs);
        doReturn(expectedMRSUsers).when(spy).getAllUsers();
        final List<MRSUser> actualMRSUsers = spy.searchStaff("", "", "", "", "", "admin");
        assertEquals(0, actualMRSUsers.size());
    }

    @Test
    public void shouldReturnEmptyIfSearchDoesntReturnAnyResults() {
        final List<MRSUser> expectedMRSUsers = Arrays.asList(new MRSUser().person(new MRSPerson().firstName("firstNm")).id("1234"));
        AllStaffs spy = spy(allStaffs);
        doReturn(expectedMRSUsers).when(spy).getAllUsers();
        final List<MRSUser> actualMRSUsers = spy.searchStaff("123", "first", "middle", "", "01234", "");
        assertEquals(0, actualMRSUsers.size());
    }
}
