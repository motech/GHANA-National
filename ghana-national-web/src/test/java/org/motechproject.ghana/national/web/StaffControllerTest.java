package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.web.helper.StaffHelper;
import org.motechproject.ghana.national.service.EmailTemplateService;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.web.form.StaffForm;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
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
import static org.motechproject.ghana.national.domain.Constants.PERSON_ATTRIBUTE_TYPE_EMAIL;
import static org.motechproject.ghana.national.domain.Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER;
import static org.motechproject.ghana.national.domain.Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE;

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

    StaffHelper staffHelper;

    @Before
    public void setUp() {
        initMocks(this);
        staffHelper = new StaffHelper();
        controller = new StaffController(mockStaffService, mockMessageSource, mockEmailTemplateService, mockIdentifierGenerationService, staffHelper);
        mockBindingResult = mock(BindingResult.class);
    }

    @Test
    public void shouldReturnNewUserForm() {
        ModelMap model = mock(ModelMap.class);
        Map<String, String> roles = new HashMap<String, String>();

        when(mockStaffService.fetchAllRoles()).thenReturn(roles);

        String view = controller.newStaff(model);

        assertEquals(StaffController.NEW_STAFF_URL, view);
        ArgumentCaptor<StaffForm> captor = ArgumentCaptor.forClass(StaffForm.class);
        verify(model).addAttribute("roles", roles);
        verify(model).addAttribute(eq(StaffController.STAFF_FORM), captor.capture());
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
        final MRSUser mrsUser = form.createUser();
        when(mockStaffService.saveUser(any(MRSUser.class))).thenReturn(test);
        when(mockStaffService.getUserById(openMRSuser.getSystemId())).thenReturn(mrsUser);

        String view = controller.create(form, bindingResult, model);

        assertEquals(StaffController.EDIT_STAFF_URL, view);
        verify(model).put("userId", "1234");
        verify(model).put("userName", "Jack H Daniels");

        ArgumentCaptor<MRSUser> captor = ArgumentCaptor.forClass(MRSUser.class);
        verify(mockStaffService).saveUser(captor.capture());
        MRSUser captured = captor.getValue();
        assertEquals(form.getRole(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(form.getEmail(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(form.getPhoneNumber(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
        assertEquals(email, captured.getUserName());
    }

    @Test
    public void shouldAddNewNonAdminUser() throws UserAlreadyExistsException {

        String userId = "1234";
        StaffForm form = new StaffForm();
        form.setEmail("jack@daniels.com");
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setStaffId(userId);
        form.setRole(StaffType.Role.COMMUNITY_HEALTH_OPERATOR.key());

        ModelMap model = mock(ModelMap.class);
        final org.openmrs.User openMRSuser = new org.openmrs.User();
        openMRSuser.setSystemId(userId);
        Map test = new HashMap() {{
            put("openMRSUser", openMRSuser);
            put("password", "P@ssw0rd");
        }};
        when(mockStaffService.saveUser(any(MRSUser.class))).thenReturn(test);
        when(mockIdentifierGenerationService.newStaffId()).thenReturn(userId);
        final MRSUser mrsUser = new MRSUser().systemId(userId);
        when(mockStaffService.getUserById(openMRSuser.getSystemId())).thenReturn(mrsUser);

        String view = controller.create(form, mockBindingResult, model);

        assertEquals(StaffController.EDIT_STAFF_URL, view);
        verify(model).put("userId", userId);
        verify(model).put("userName", "Jack H Daniels");

        ArgumentCaptor<MRSUser> captor = ArgumentCaptor.forClass(MRSUser.class);
        verify(mockStaffService).saveUser(captor.capture());
        MRSUser captured = captor.getValue();
        assertEquals(userId, captured.getSystemId());
        assertEquals(form.getRole(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(form.getEmail(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(form.getPhoneNumber(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
//        assertNull(captured.getUserName());
    }

    private String getAttrValue(MRSUser mrsUser, String name) {
        for (Attribute userAttribute : mrsUser.getAttributes())
            if (userAttribute.name().equalsIgnoreCase(name))
                return userAttribute.value();
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

        when(mockStaffService.saveUser(any(MRSUser.class))).thenThrow(new UserAlreadyExistsException());
        when(mockBindingResult.getModel()).thenReturn(boundModel);
        when(mockStaffService.fetchAllRoles()).thenReturn(roles);

        String view = controller.create(form, mockBindingResult, model);

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
        MRSUser user1 = new MRSUser().systemId(staffID);
        user1.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, phoneNumber));
        user1.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, email));
        user1.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role));
        user1.firstName(firstName).middleName(middleName).lastName(lastName).id(staffID);
        when(mockStaffService.searchStaff(staffID, firstName, middleName, lastName, phoneNumber, role)).thenReturn(Arrays.asList(user1));

        controller.search(staffForm, modelMap);

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
        String email = null;
        String phoneNumber = "0987654321";
        String role = "CHV";

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("Id", staffId);

        MRSUser mrsUser = mock(MRSUser.class);
        when(mockStaffService.getUserById(staffId)).thenReturn(mrsUser);
        when(mrsUser.getSystemId()).thenReturn(staffId);
        when(mrsUser.getFirstName()).thenReturn(firstName);
        when(mrsUser.getMiddleName()).thenReturn(middleName);
        when(mrsUser.getLastName()).thenReturn(lastName);
        when(mrsUser.getSecurityRole()).thenReturn(role);
        when(mrsUser.getAttributes()).thenReturn(asList(new Attribute(PERSON_ATTRIBUTE_TYPE_EMAIL, email),
                new Attribute(PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, phoneNumber), new Attribute(PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role)));

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("Id", staffId);
        String editFormName = controller.edit(modelMap, mockHttpServletRequest);

        assertThat(editFormName, is(StaffController.EDIT_STAFF_URL));
        StaffForm expectedStaffForm = createStaffForm(staffId, firstName, middleName, lastName, email, phoneNumber, role);
        StaffForm staffForm = (StaffForm) modelMap.get(StaffController.STAFF_FORM);

        assertStaffForm(expectedStaffForm, staffForm);
    }

    private void assertStaffForm(StaffForm expectedStaffForm, StaffForm staffForm) {
        assertThat(staffForm.getFirstName(), is(equalTo(expectedStaffForm.getFirstName())));
        assertThat(staffForm.getLastName(), is(equalTo(expectedStaffForm.getLastName())));
        assertThat(staffForm.getMiddleName(), is(equalTo(expectedStaffForm.getMiddleName())));
        assertThat(staffForm.getRole(), is(equalTo(expectedStaffForm.getRole())));
        assertThat(staffForm.getStaffId(), is(equalTo(expectedStaffForm.getStaffId())));
        assertThat(staffForm.getEmail(), is(equalTo(expectedStaffForm.getEmail())));
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

    @Test
    public void shouldUpdateUser() throws UserAlreadyExistsException {
        String id = "1";
        String staffId = "112";
        String first = "first";
        String last = "last";
        String phoneNumber = "0123456789";
        String hpo = "HPO";
        String email = "";
        StaffForm staffForm = new StaffForm(id, staffId, first, "", last, email, phoneNumber, hpo);
        ModelMap modelMap = new ModelMap();
        MRSUser mockMRSUser = mock(MRSUser.class);
        when(mockStaffService.getUserById(staffId)).thenReturn(mockMRSUser);

        String result = controller.update(staffForm, mockBindingResult, modelMap);
        ArgumentCaptor<MRSUser> captor = ArgumentCaptor.forClass(MRSUser.class);
        verify(mockStaffService).updateUser(captor.capture());
        assertThat(result, is(StaffController.EDIT_STAFF_URL));

        MRSUser actualUser = captor.getValue();
        assertThat(actualUser.getId(), is(id));
        assertThat(actualUser.getSystemId(), is(staffId));
        assertThat(actualUser.getFirstName(), is(first));
        assertThat(actualUser.getLastName(), is(last));
        assertThat(actualUser.getAttributes().size(), is(3));
        assertThat(attrValue(actualUser.getAttributes(), PERSON_ATTRIBUTE_TYPE_EMAIL), is(email));
        assertThat(attrValue(actualUser.getAttributes(), PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER), is(phoneNumber));
        assertThat(attrValue(actualUser.getAttributes(), PERSON_ATTRIBUTE_TYPE_STAFF_TYPE), is(hpo));

        assertNotNull(modelMap.get("roles"));
        assertNotNull(modelMap.get(StaffController.STAFF_FORM));
    }

    private String attrValue(List<Attribute> attributes, String key) {
        List<Attribute> filteredItems = select(attributes, having(on(Attribute.class).name(), equalTo(key)));
        return isNotEmpty(filteredItems) ? filteredItems.get(0).value() : null;
    }
}


