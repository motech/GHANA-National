package org.motechproject.ghana.national;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:summa.xml"})
public class Summa {
    @Autowired
    ConfigFileMonitor configFileMonitor;

    @Test
    public void shouldTest() {
        System.out.println(configFileMonitor);
    }
}
