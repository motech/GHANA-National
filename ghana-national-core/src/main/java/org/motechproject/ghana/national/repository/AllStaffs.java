package org.motechproject.ghana.national.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.tools.StartsWithMatcher;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

@Repository
public class AllStaffs {
    public List<MRSUser> searchStaff(String systemId, String firstName, String middleName, String lastName, String phoneNumber, String role, List<MRSUser> allUsers) {
        allUsers = filterUsers(on(MRSUser.class).getSystemId(), systemId, allUsers);
        allUsers = filterUsers(on(MRSUser.class).getPerson().getFirstName(), firstName, allUsers);
        allUsers = filterUsers(on(MRSUser.class).getPerson().getMiddleName(), middleName, allUsers);
        allUsers = filterUsers(on(MRSUser.class).getPerson().getLastName(), lastName, allUsers);
        allUsers = filterByPhone(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, phoneNumber, allUsers);
        allUsers = filterByRole(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, role, allUsers);
        Collections.sort(allUsers, new UserFirstNameComparator());
        return allUsers;
    }

    private List<MRSUser> filterUsers(String field, String matcher, List<MRSUser> filteredMRSUsers) {
        return (StringUtils.isNotEmpty(matcher)) ? filter(having(field, StartsWithMatcher.ignoreCaseStartsWith(matcher)), filteredMRSUsers) : filteredMRSUsers;
    }


    private List<MRSUser> filterByRole(String searchAttribute, String searchCriteria, List<MRSUser> filteredMRSUsers) {
        if (StringUtils.isEmpty(searchCriteria)) {
            return filteredMRSUsers;
        }
        ArrayList<MRSUser> filteredList = new ArrayList<MRSUser>();
        for (MRSUser filteredMRSUser : filteredMRSUsers) {
            List<Attribute> attributeSearchResults = filter(having(on(Attribute.class).name(), equalTo(searchAttribute)), filteredMRSUser.getPerson().getAttributes());
            if (CollectionUtils.isNotEmpty(attributeSearchResults) && attributeSearchResults.get(0).value().trim().equalsIgnoreCase(searchCriteria)) {
                filteredList.add(filteredMRSUser);
            }
        }
        return filteredList;
    }

    private List<MRSUser> filterByPhone(String searchAttribute, String searchCriteria, List<MRSUser> filteredMRSUsers) {
        ArrayList<MRSUser> filteredList = new ArrayList<MRSUser>();
        for (MRSUser filteredMRSUser : filteredMRSUsers) {
            List<Attribute> attributeSearchResults = filter(having(on(Attribute.class).name(), equalTo(searchAttribute)), filteredMRSUser.getPerson().getAttributes());
            if (CollectionUtils.isNotEmpty(attributeSearchResults) && attributeSearchResults.get(0).value().startsWith(searchCriteria)) {
                filteredList.add(filteredMRSUser);
            }
        }
        return (CollectionUtils.isEmpty(filteredList)) ? filteredMRSUsers : filteredList;
    }

    private class UserFirstNameComparator implements Comparator<MRSUser> {
        @Override
        public int compare(MRSUser user1, MRSUser user2) {
            return user2.getPerson().getFirstName().compareTo(user1.getPerson().getFirstName());
        }
    }
}
