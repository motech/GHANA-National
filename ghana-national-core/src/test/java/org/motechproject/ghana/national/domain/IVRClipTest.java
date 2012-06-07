package org.motechproject.ghana.national.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IVRClipTest {
    @Test
    public void shouldReturnClipName_GivenScheduleAlertDetails(){
        assertThat(new IVRClip().name("scheduleName", AlertWindow.DUE), is(equalTo("prompt_scheduleName_Due")));
    }
}
