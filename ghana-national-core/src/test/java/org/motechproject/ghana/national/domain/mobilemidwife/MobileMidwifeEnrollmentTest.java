package org.motechproject.ghana.national.domain.mobilemidwife;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.util.DateUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MobileMidwifeEnrollmentTest {

    @Test
    public void shouldCreateCampaignRequestFromEnrollment() {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "patientId";
        String messageStartWeekKey = "55";
        MobileMidwifeEnrollment mobileMidwifeEnrollment = MobileMidwifeEnrollment.newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType).setMessageStartWeek(messageStartWeekKey);

        CampaignRequest campaignRequest = mobileMidwifeEnrollment.createCampaignRequest();
        assertThat(campaignRequest.campaignName(), is(serviceType.name()));
        assertThat(campaignRequest.externalId(), is(patientId));
        assertThat(campaignRequest.referenceDate(), is(DateUtil.today()));
        assertThat(campaignRequest.startOffset(), is(MessageStartWeek.findBy(messageStartWeekKey).getWeek()));

        DateTime enrolledDateTime = mobileMidwifeEnrollment.getEnrollmentDateTime();
        assertThat(campaignRequest.reminderTime(), is(new Time(enrolledDateTime.getHourOfDay(), enrolledDateTime.getMinuteOfHour())));
    }
}
