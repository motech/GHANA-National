package org.motechproject.ghana.national.domain.mobilemidwife;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MobileMidwifeCampaignTest {
    @Test
    public void shouldReturnCampaignName_GivenServiceTypeAndMedium() {
        assertThat(MobileMidwifeCampaign.getName(ServiceType.CHILD_CARE, Medium.SMS), is("CHILD_CARE_SMS"));
        assertThat(MobileMidwifeCampaign.getName(ServiceType.PREGNANCY, Medium.SMS), is("PREGNANCY_SMS"));
        assertThat(MobileMidwifeCampaign.getName(ServiceType.CHILD_CARE, Medium.VOICE), is("CHILD_CARE_VOICE"));
        assertThat(MobileMidwifeCampaign.getName(ServiceType.PREGNANCY, Medium.VOICE), is("PREGNANCY_VOICE"));
    }
}
