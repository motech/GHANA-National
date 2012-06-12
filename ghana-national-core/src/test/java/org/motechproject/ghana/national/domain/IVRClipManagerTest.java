package org.motechproject.ghana.national.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IVRClipManagerTest {

    private IVRClipManager ivrClipManager;

    @Before
    public void setUp() {
        ivrClipManager = new IVRClipManager();
        ReflectionTestUtils.setField(ivrClipManager, "resourceBaseUrl", "http://localhost:8080/ghana-national/resource/stream/");
    }

    @Test
    public void shouldReturnURLForMessage() {
        String url = ivrClipManager.urlFor("welcome-message", Language.FAN);
        assertThat(url, is("http://localhost:8080/ghana-national/resource/stream/FAN/welcome-message.wav"));
    }

}
