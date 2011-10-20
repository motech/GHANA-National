package org.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.Facility;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext.xml"})
public class AllFacilitiesTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("couchDbConnector")
    @Autowired
    protected CouchDbConnector dbConnector;

    @Autowired
    private AllFacilities facilityRepository;

    @Test
    public void shouldSaveAFacility() throws Exception {
        final String facilityName = "name";
        Facility facility = new Facility(new org.motechproject.mrs.model.Facility( facilityName)).phoneNumber(1234567890);
        final int initialSize = facilityRepository.getAll().size();

        facilityRepository.add(facility);

        final List<Facility> facilities = facilityRepository.getAll();
        final int actualSize = facilities.size();
        final Facility actualFacility = facilities.iterator().next();
        assertThat(actualSize, is(equalTo(initialSize + 1)));
        assertThat(actualFacility.getPhoneNumber(), is(equalTo(1234567890)));
        assertThat(actualFacility.name(), is(equalTo( facilityName)));
    }

    @After
    public void tearDown() {
        List<Facility> all = facilityRepository.getAll();
        for (Facility facility : all)
            facilityRepository.remove(facility);
    }
}
