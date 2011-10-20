package org.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.Facility;
import org.junit.After;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext.xml"})
public class AllFacilitiesTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("couchDbConnector")
    @Autowired
    protected CouchDbConnector dbConnector;

    @Autowired
    private AllFacilities facilityRepository;

    @Test
    public void shouldSaveAFacility() {
        Facility sampleFacility = new Facility(1234666, null, null, null, "1234566", 1234566);
        final int initialSize = facilityRepository.getAll().size();

        facilityRepository.add(sampleFacility);

        final List<Facility> facilities = facilityRepository.getAll();
        final int actualSize = facilities.size();
        final Facility facility = facilities.iterator().next();
        assertThat(actualSize, is(equalTo(initialSize + 1)));
        assertThat(facility.getFacilityId(), is(equalTo(sampleFacility.getFacilityId())));
        assertThat(facility.getMotechFacilityId(), is(equalTo(sampleFacility.getMotechFacilityId())));
        assertThat(facility.getPhoneNumber(), is(equalTo(sampleFacility.getPhoneNumber())));
    }

    @After
    public void tearDown() {
        List<Facility> all = facilityRepository.getAll();
        for (Facility facility : all)
            facilityRepository.remove(facility);

    }
}
