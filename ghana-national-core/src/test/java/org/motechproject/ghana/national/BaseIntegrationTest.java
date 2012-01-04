package org.motechproject.ghana.national;

import org.ektorp.CouchDbConnector;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public abstract class BaseIntegrationTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("couchDbConnector")
    @Autowired
    protected CouchDbConnector dbConnector;
}
