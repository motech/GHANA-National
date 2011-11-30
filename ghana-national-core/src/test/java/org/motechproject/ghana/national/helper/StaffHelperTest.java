package org.motechproject.ghana.national.helper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.User;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StaffHelperTest {

    StaffHelper staffHelper;

    @Before
    public void setUp() {
        staffHelper = new StaffHelper();
    }

    @Test
    public void shouldGetEmailFromUserIfPresent() {
        User user = new User();
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, "0123456789"));
        String email = "a@a.com";
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, email));
        user.setAttributes(attributes);

        assertThat(staffHelper.getEmail(user), is(email));
    }

    @Test
    public void shouldReturnEmptyIfEmailIsNotFound() {
        assertThat(staffHelper.getEmail(new User()), is(""));
    }

    @Test
    public void shouldGetPhoneNumberFromUserIfPresent() {
        User user = new User();
        final String expectedPhoneNumber = "0123456789";
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, expectedPhoneNumber));
        user.setAttributes(attributes);

        assertThat(staffHelper.getPhoneNumber(user), is(expectedPhoneNumber));
    }

    @Test
    public void shouldReturnEmptyIfPhoneNumberIsNotFound() {
        assertThat(staffHelper.getPhoneNumber(new User()), is(""));
    }

    @Test
    public void shouldGetRoleFromUserIfPresent() {
        User user = new User();
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        String role = "Admin";
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role));
        user.setAttributes(attributes);
        final String actualRole = staffHelper.getRole(user);

        assertThat(actualRole, is(role));
    }

    @Test
    public void shouldReturnEmptyIfRoleIsNotFound() {
        assertThat(staffHelper.getRole(new User()), is(""));
    }

}
