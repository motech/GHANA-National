package org.motechproject.ghana.national.helper;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.web.helper.StaffHelper;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;

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
        MRSUser mrsUser = new MRSUser();
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, "0123456789"));
        String email = "a@a.com";
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, email));
        mrsUser.setAttributes(attributes);

        assertThat(staffHelper.getEmail(mrsUser), is(email));
    }

    @Test
    public void shouldReturnEmptyIfEmailIsNotFound() {
        assertThat(staffHelper.getEmail(new MRSUser()), is(""));
    }

    @Test
    public void shouldGetPhoneNumberFromUserIfPresent() {
        MRSUser mrsUser = new MRSUser();
        final String expectedPhoneNumber = "0123456789";
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, expectedPhoneNumber));
        mrsUser.setAttributes(attributes);

        assertThat(staffHelper.getPhoneNumber(mrsUser), is(expectedPhoneNumber));
    }

    @Test
    public void shouldReturnEmptyIfPhoneNumberIsNotFound() {
        assertThat(staffHelper.getPhoneNumber(new MRSUser()), is(""));
    }

    @Test
    public void shouldGetRoleFromUserIfPresent() {
        MRSUser mrsUser = new MRSUser();
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        String role = "Admin";
        attributes.add(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role));
        mrsUser.setAttributes(attributes);
        final String actualRole = staffHelper.getRole(mrsUser);

        assertThat(actualRole, is(role));
    }

    @Test
    public void shouldReturnEmptyIfRoleIsNotFound() {
        assertThat(staffHelper.getRole(new MRSUser()), is(""));
    }

}
