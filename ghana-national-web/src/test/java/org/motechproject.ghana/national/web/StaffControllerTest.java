package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.helper.StaffHelper;
import org.motechproject.ghana.national.service.EmailTemplateService;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.web.form.StaffForm;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.User;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StaffControllerTest {

    StaffController controller;
    @Mock
    StaffService mockStaffService;
    @Mock
    MessageSource mockMessageSource;
    @Mock
    EmailTemplateService mockEmailTemplateService;
    @Mock
    BindingResult mockBindingResult;
    @Mock
    IdentifierGenerationService mockIdentifierGenerationService;
    @Mock
    StaffHelper mockStaffHelper;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new StaffController(mockStaffService, mockMessageSource, mockEmailTemplateService, mockIdentifierGenerationService, mockStaffHelper);
        mockBindingResult = mock(BindingResult.class);
    }

    @Test
    public void shouldReturnNewUserForm() {
        ModelMap model = mock(ModelMap.class);
        Map<String, String> roles = new HashMap<String, String>();

        when(mockStaffService.fetchAllRoles()).thenReturn(roles);

        String view = controller.newUser(model);

        assertEquals(StaffController.NEW_STAFF_URL, view);
        ArgumentCaptor<StaffForm> captor = ArgumentCaptor.forClass(StaffForm.class);
        verify(model).addAttribute("roles", roles);
        verify(model).addAttribute(eq("createStaffForm"), captor.capture());
        StaffForm captured = captor.getValue();
        assertNotNull(captured);
    }

    @Test
    public void shouldAddNewAdminUser() throws UserAlreadyExistsException {
        StaffForm form = new StaffForm();
        final String email = "jack@daniels.com";
        form.setEmail(email);
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setRole(StaffType.Role.CALL_CENTER_ADMIN.key());

        BindingResult bindingResult = mock(BindingResult.class);
        ModelMap model = mock(ModelMap.class);
        final org.openmrs.User openMRSuser = new org.openmrs.User();
        openMRSuser.setSystemId("1234");
        Map test = new HashMap() {{
            put("openMRSUser", openMRSuser);
            put("password", "P@ssw0rd");
        }};
        when(mockStaffService.saveUser(any(User.class))).thenReturn(test);

        String view = controller.createUser(form, bindingResult, model);

        assertEquals(StaffController.SUCCESS, view);
        verify(model).put("userId", "1234");
        verify(model).put("userName", "Jack H Daniels");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(mockStaffService).saveUser(captor.capture());
        User captured = captor.getValue();
        assertEquals(form.getRole(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(form.getEmail(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(form.getPhoneNumber(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
        assertEquals(email, captured.getUserName());
    }

    @Test
    public void shouldAddNewNonAdminUser() throws UserAlreadyExistsException {
        StaffForm form = new StaffForm();
        form.setEmail("jack@daniels.com");
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setRole(StaffType.Role.COMMUNITY_HEALTH_OPERATOR.key());

        ModelMap model = mock(ModelMap.class);
        String userId = "1234";
        final org.openmrs.User openMRSuser = new org.openmrs.User();
        openMRSuser.setSystemId("1234");
        Map test = new HashMap() {{
            put("openMRSUser", openMRSuser);
            put("password", "P@ssw0rd");
        }};
        when(mockStaffService.saveUser(any(User.class))).thenReturn(test);
        when(mockIdentifierGenerationService.newStaffId()).thenReturn(userId);

        String view = controller.createUser(form, mockBindingResult, model);

        assertEquals(StaffController.SUCCESS, view);
        verify(model).put("userId", "1234");
        verify(model).put("userName", "Jack H Daniels");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(mockStaffService).saveUser(captor.capture());
        User captured = captor.getValue();
        assertEquals(userId, captured.getId());
        assertEquals(form.getRole(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(form.getEmail(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(form.getPhoneNumber(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
        assertNull(captured.getUserName());
    }

    private String getAttrValue(User user, String name) {
        for (Attribute userAttribute : user.getAttributes())
            if (userAttribute.name().equalsIgnoreCase(name)) return userAttribute.value();
        return null;
    }

    @Test
    public void shouldReturnErrorMessageIfUserAlreadyExists() throws UserAlreadyExistsException {
        StaffForm form = new StaffForm();
        form.setEmail("jack@daniels.com");
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setRole(StaffType.Role.CALL_CENTER_ADMIN.key());


        ModelMap model = mock(ModelMap.class);
        Map<String, String> roles = new HashMap<String, String>();
        roles.put("role1", "role1");
        Map<String, Object> boundModel = new HashMap<String, Object>();

        when(mockStaffService.saveUser(any(User.class))).thenThrow(new UserAlreadyExistsException());
        when(mockBindingResult.getModel()).thenReturn(boundModel);
        when(mockStaffService.fetchAllRoles()).thenReturn(roles);

        String view = controller.createUser(form, mockBindingResult, model);

        assertEquals(StaffController.NEW_STAFF_URL, view);
        verify(model).mergeAttributes(boundModel);
        verify(model).addAttribute("roles", roles);
        verify(mockBindingResult).addError(any(FieldError.class));
    }

    @Test
    public void shouldSearchForUser() {
        String staffID = "1";
        String firstName = "TEST";
        String middleName = "TEST";
        String lastName = "TEST";
        String email = "TEST@gmail.COM";
        String phoneNumber = "0987654321";
        String role = "System Developer";

        StaffForm staffForm = new StaffForm();
        staffForm.setStaffId(staffID);
        staffForm.setFirstName(firstName);
        staffForm.setMiddleName(middleName);
        staffForm.setLastName(lastName);
        staffForm.setPhoneNumber(phoneNumber);
        staffForm.setRole(role);

        ModelMap modelMap = new ModelMap();
        User user1 = new User();
        user1.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, phoneNumber));
        user1.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, email));
        user1.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role));
        user1.firstName(firstName).middleName(middleName).lastName(lastName).id(staffID);
        when(mockStaffService.searchStaff(staffID, firstName, middleName, lastName, phoneNumber, role)).thenReturn(Arrays.asList(user1));
        when(mockStaffHelper.getEmail(user1)).thenReturn(email);
        when(mockStaffHelper.getPhoneNumber(user1)).thenReturn(phoneNumber);
        when(mockStaffHelper.getRole(user1)).thenReturn(role);

        controller.searchStaff(staffForm, modelMap);

        verify(mockStaffService).searchStaff(staffForm.getStaffId(), staffForm.getFirstName(), staffForm.getMiddleName(),
                staffForm.getLastName(), staffForm.getPhoneNumber(), staffForm.getRole());

        final List<StaffForm> actualRequestedUsers = (List<StaffForm>) modelMap.get(StaffController.REQUESTED_STAFFS);
        assertEquals(1, actualRequestedUsers.size());
        final StaffForm form = actualRequestedUsers.get(0);
        assertEquals(form.getStaffId(), staffID);
        assertEquals(form.getEmail(), email);
        assertEquals(form.getFirstName(), firstName);
        assertEquals(form.getLastName(), lastName);
        assertEquals(form.getMiddleName(), middleName);
        assertEquals(form.getPhoneNumber(), phoneNumber);
        assertEquals(form.getRole(), role);
    }


    @Test
    public void shouldReturnDetailsOfStaffToEdit() {
        String staffId = "123";
        String firstName = "fname";
        String middleName = "maname";
        String lastName = "lname";
        String email = "test@gmail.com";
        String phoneNumber = "0987654321";
        String role = "CHV";
        final String EDIT_STAFF_URL = "staffs/edit";
        final String EDIT_STAFF_FORM = "editStaffForm";

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("Id", staffId);

        User user = mock(User.class);
        when(mockStaffService.getUserById(staffId)).thenReturn(user);
        when(user.getFirstName()).thenReturn(firstName);
        when(user.getMiddleName()).thenReturn(middleName);
        when(user.getLastName()).thenReturn(lastName);
        when(user.getSecurityRole()).thenReturn(role);

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("Id", staffId);
        String editFormName = controller.editStaffForm(modelMap, mockHttpServletRequest);

        assertThat(editFormName, is(EDIT_STAFF_URL));
        StaffForm expectedStaffForm = createStaffForm(staffId, firstName, middleName, lastName, email, phoneNumber, role);
        StaffForm staffForm = (StaffForm) modelMap.get(EDIT_STAFF_FORM);

        assertStaffForm(expectedStaffForm, staffForm);
    }

    private void assertStaffForm(StaffForm expectedStaffForm, StaffForm staffForm) {
        assertThat(staffForm.getFirstName(), is(equalTo(expectedStaffForm.getFirstName())));
    }

    private StaffForm createStaffForm(String staffId, String firstName, String middleName, String lastName, String email, String phoneNumber, String role) {
        StaffForm staffForm = new StaffForm();
        staffForm.setStaffId(staffId);
        staffForm.setFirstName(firstName);
        staffForm.setMiddleName(middleName);
        staffForm.setLastName(lastName);
        staffForm.setEmail(email);
        staffForm.setPhoneNumber(phoneNumber);
        staffForm.setRole(role);
        return staffForm;
    }
}


