package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.repository.AllStaffs;
import org.motechproject.ghana.national.repository.EmailGateway;
import org.motechproject.ghana.national.repository.IdentifierGenerator;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.services.OpenMRSUserAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Mock
    private IdentifierGenerator mockIdentifierGenerator;

    @Before
    public void setUp() {
        initMocks(this);
        service = new StaffService(mockAllStaffs, mockEmailGateway, mockIdentifierGenerator);
    }

    @Test
    public void shouldFetchAllRoles() {
        service.fetchAllRoles();
        verify(mockAllStaffs).fetchAllRoles();
    }

    @Test
    public void shouldSaveUserAndSendEmailIfAdmin() throws UserAlreadyExistsException {
        final MRSUser mrsUser = new MRSUser();
        final MRSUser openMRSUser = new MRSUser().person(new MRSPerson()
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, StaffType.Role.SUPER_ADMIN.key())));
        final String password = "P@ssw0rd";
        Map test = new HashMap() {{
            put(OpenMRSUserAdapter.USER_KEY, openMRSUser);
            put("password", password);
        }};

        String staffId = "12";
        mrsUser.systemId(staffId);
        when(mockIdentifierGenerator.newStaffId()).thenReturn(staffId);
        when(mockAllStaffs.saveUser(mrsUser)).thenReturn(test);

        service.saveUser(mrsUser);

        verify(mockAllStaffs).saveUser(mrsUser);
        verify(mockEmailGateway).sendEmailUsingTemplates(openMRSUser.getUserName(), password);
    }

    @Test
    public void shouldSaveUserAndShouldNotSendEmailIfNonAdmin() throws UserAlreadyExistsException {
        final MRSUser mrsUser = new MRSUser();
        final MRSUser openMRSUser = new MRSUser().person(new MRSPerson()
                .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, StaffType.Role.HEALTH_EXTENSION_WORKER.key())));
        Map test = new HashMap() {{
            put(OpenMRSUserAdapter.USER_KEY, openMRSUser);
            put("password", "P@ssw0rd");
        }};

        String staffId = "12";
        mrsUser.systemId(staffId);
        when(mockIdentifierGenerator.newStaffId()).thenReturn(staffId);
        when(mockAllStaffs.saveUser(mrsUser)).thenReturn(test);

        service.saveUser(mrsUser);

        verify(mockAllStaffs).saveUser(mrsUser);
        verify(mockEmailGateway, never()).sendEmailUsingTemplates(anyString(), anyString());
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
