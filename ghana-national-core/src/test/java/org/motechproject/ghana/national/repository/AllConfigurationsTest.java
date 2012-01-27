package org.motechproject.ghana.national.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AllConfigurationsTest  extends BaseIntegrationTest {
    @Autowired
    AllConfigurations allConfigurations;

    @Test
    public void shouldReturnConfigurationForGivenConfigurationName() {
        String configurationName = "someConfigurationName";
        String configurationValue = "someConfigurationValue";
        Configuration configuration = saveConfigurationToDB(configurationName, configurationValue);
        Configuration configurationFromDB = allConfigurations.getConfigurationValue(configurationName);
        assertThat(configurationFromDB.getPropertyName(), is(equalTo(configuration.getPropertyName())));
        assertThat(configurationFromDB.getValue(), is(equalTo(configuration.getValue())));
    }

    private Configuration saveConfigurationToDB(String configurationName, String configurationValue) {
        Configuration configuration = new Configuration(configurationName, configurationValue);
        allConfigurations.add(configuration);
        return configuration;
    }

    @After
    public void tearDown() {
        List<Configuration> all = allConfigurations.getAll();
        for (Configuration configuration : all)
            allConfigurations.remove(configuration);
    }
}
