package org.motechproject.ghana.national.domain.ivr;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MobileMidwifeAudioClipsTest {

    @Test
    public void shouldReturnTheAudioClipGivenProgramNameAndWeekNumber(){
        assertThat(MobileMidwifeAudioClips.instance("pregnancy","7","EN"),is(equalTo(MobileMidwifeAudioClips.PREGNANCY_WEEK_7_EN)));
    }

    @Test
    public void shouldContainEnumForAllLangAndWeeks() throws Exception {
        for(int i=1;i<=52;i++) {
            assertNotNull(MobileMidwifeAudioClips.instance("CHILD_CARE", String.valueOf(i), "EN"));
            assertNotNull(MobileMidwifeAudioClips.instance("CHILD_CARE", String.valueOf(i), "KAS"));
            assertNotNull(MobileMidwifeAudioClips.instance("CHILD_CARE", String.valueOf(i), "NAN"));
            assertNotNull(MobileMidwifeAudioClips.instance("CHILD_CARE", String.valueOf(i), "FAN"));
        }

        for(int i=5;i<=40;i++) {
            assertNotNull(MobileMidwifeAudioClips.instance("PREGNANCY", String.valueOf(i), "EN"));
            assertNotNull(MobileMidwifeAudioClips.instance("PREGNANCY", String.valueOf(i), "KAS"));
            assertNotNull(MobileMidwifeAudioClips.instance("PREGNANCY", String.valueOf(i), "NAN"));
            assertNotNull(MobileMidwifeAudioClips.instance("PREGNANCY", String.valueOf(i), "FAN"));
        }
    }
}
