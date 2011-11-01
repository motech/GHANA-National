package org.motechproject.ghana.national.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class IdentifierServiceIT extends AbstractJUnit4SpringContextTests {

    @Autowired
    private IdentifierService identifierService;

    @Test
    @Ignore
    public void shouldLoadIdGen() {
        assertNotNull(identifierService.getIdentifierAdaptor().getGeneratorService());
    }

}
