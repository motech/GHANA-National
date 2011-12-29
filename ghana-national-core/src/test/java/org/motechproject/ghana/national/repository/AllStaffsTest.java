package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllStaffsTest {
    private AllStaffs allStaffs;

    @Before
    public void setUp() {
        allStaffs = new AllStaffs();
    }

    @Test
    public void shouldSearchForUsersWithRespectiveSearchCriteria() {
        final String middleName = "middleName";
        MRSPerson mrsPerson = new MRSPerson().firstName("firstName").middleName(middleName)
                .attributes(Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, "0123456789")));
        final MRSUser user1 = new MRSUser().systemId("123").person(mrsPerson);

        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().person(new MRSPerson().firstName("first")).systemId("1234"));
        final List<MRSUser> actualMRSUsers = allStaffs.searchStaff("123", "first", middleName, "", "01234", "", expectedMRSUsers);
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
        final List<MRSUser> actualMRSUsers = allStaffs.searchStaff(null, null, middileName, null, null, null, expectedMRSUsers);
        assertEquals(2, actualMRSUsers.size());
        assertEquals(firstName2, actualMRSUsers.get(0).getPerson().getFirstName());
        assertEquals(firstName1, actualMRSUsers.get(1).getPerson().getFirstName());
    }

    @Test
    public void shouldSearchForUsersWithRespectiveRoles() {
        List<Attribute> attributeList = Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, "admin"));
        final MRSUser user1 = new MRSUser().id("123").person(new MRSPerson().firstName("firstName").middleName("middleName").attributes(attributeList));
        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().person(new MRSPerson().firstName("firstNm")).id("1234"));
        final List<MRSUser> actualMRSUsers = allStaffs.searchStaff("", "", "", "", "", "admin", expectedMRSUsers);
        assertEquals(1, actualMRSUsers.size());
    }

    @Test
    public void shouldSearchForUsersWithRespectiveRolesAndReturnEmptyIfRolesAreEmpty() {
        List<Attribute> attributeList = Arrays.asList(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, ""));
        final MRSUser user1 = new MRSUser().id("123").person(new MRSPerson().firstName("firstName").middleName("middleName").attributes(attributeList));
        final List<MRSUser> expectedMRSUsers = Arrays.asList(user1, new MRSUser().person(new MRSPerson().firstName("firstNm")).id("1234"));
        final List<MRSUser> actualMRSUsers = allStaffs.searchStaff("", "", "", "", "", "admin", expectedMRSUsers);
        assertEquals(0, actualMRSUsers.size());
    }

    @Test
    public void shouldReturnEmptyIfSearchDoesntReturnAnyResults() {
        final List<MRSUser> expectedMRSUsers = Arrays.asList(new MRSUser().person(new MRSPerson().firstName("firstNm")).id("1234"));
        final List<MRSUser> actualMRSUsers = allStaffs.searchStaff("123", "first", "middle", "", "01234", "", expectedMRSUsers);
        assertEquals(0, actualMRSUsers.size());
    }
}
