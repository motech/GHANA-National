package org.motechproject.ghana.national.domain.ivr;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MobileMidwifeAudioClipsTest {

    @Test
    public void shouldReturnTheAudioClipGivenProgramNameAndWeekNumber(){
        assertThat(MobileMidwifeAudioClips.instance("pregnancy","7"),is(equalTo(MobileMidwifeAudioClips.PREGNANCY_WEEK_7)));
    }
}
