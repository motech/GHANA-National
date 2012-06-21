package org.motechproject.ghana.national.domain.ivr;

import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.AlertWindow;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AudioPromptsTest {
    @Test
    public void shouldReturnClipName_GivenScheduleAlertDetails() {
        assertThat(AudioPrompts.fileNameForCareSchedule(ScheduleNames.CWC_BCG.getName(), AlertWindow.DUE), is(equalTo("prompt_IR6")));
        assertThat(AudioPrompts.fileNameForCareSchedule(ScheduleNames.CWC_IPT_VACCINE.getName(), AlertWindow.DUE), is(equalTo("prompt_IR7")));
        assertThat(AudioPrompts.fileNameForCareSchedule(ScheduleNames.ANC_DELIVERY.getName(), AlertWindow.DUE), is(equalTo("prompt_IR1")));
    }

    @Test
    public void shouldReturnMobileMidwifeClipName_GivenProgramNameAndWeek() {
        assertThat(AudioPrompts.fileNameForMobileMidwife("pregnancy", "33"), is("prompt_pregnancy_33"));
    }
}
