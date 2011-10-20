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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;

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
        Facility sampleFacility = new Facility(1234666, null, null, null, "1234566", 1234566);
        final int initialSize = facilityRepository.getAll().size();

        facilityRepository.add(sampleFacility);

        final List<Facility> facilities = facilityRepository.getAll();
        final int actualSize = facilities.size();
        final Facility facility = facilities.iterator().next();
        assertThat(actualSize, is(equalTo(initialSize + 1)));
        assertEquals(ReflectionTestUtils.getField(sampleFacility, "motechFacilityId"), ReflectionTestUtils.getField(facility, "motechFacilityId"));
        assertEquals(ReflectionTestUtils.getField(sampleFacility, "facilityId"), ReflectionTestUtils.getField(facility, "facilityId"));
    }

    @After
    public void tearDown() {
        List<Facility> all = facilityRepository.getAll();
        for (Facility facility : all)
            facilityRepository.remove(facility);
    }
}
