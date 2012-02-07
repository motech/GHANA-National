package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.mobilemidwife.ServiceType.PREGNANCY;
import static org.motechproject.ghana.national.service.MobileMidwifeCampaign.*;

public class MobileMidwifeCampaignTest {

    MobileMidwifeCampaign campaign;

    @Mock
    MessageCampaignService mockMessageCampaignService;
    @Mock
    AllMessageCampaigns mockAllMessageCampaigns;

    @Before
    public void setUp() {
        initMocks(this);
        campaign = new MobileMidwifeCampaign(mockMessageCampaignService, mockAllMessageCampaigns);
    }

    @Test
    public void shouldStartCampaignservice() {
        MobileMidwifeEnrollment enrollment = MobileMidwifeEnrollment.newEnrollment().setServiceType(PREGNANCY).setMessageStartWeek("33").setScheduleStartDate(new DateTime());
        campaign.start(enrollment);
        verify(mockMessageCampaignService).startFor(any(CampaignRequest.class));
    }

    @Test
    public void shouldStopCampaignservice() {
        MobileMidwifeEnrollment enrollment = MobileMidwifeEnrollment.newEnrollment().setServiceType(ServiceType.CHILD_CARE)
                .setMessageStartWeek("53").setScheduleStartDate(new DateTime());
        campaign.stop(enrollment);
        verify(mockMessageCampaignService).stopAll(any(CampaignRequest.class));

    }

    @Test
    public void shouldReturnCycleStartDateForEnrollmentRegistrationDateAndDaysApplicable() {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        DateTime registrationThuFeb2_2012 = new DateTime(2012, 2, 2, 4, 15, 0);
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment().setEnrollmentDateTime(registrationThuFeb2_2012).setServiceType(serviceType);
        when(mockAllMessageCampaigns.getApplicableDaysForRepeatingCampaign(serviceType.name(), CHILDCARE_MESSAGE_NAME)).thenReturn(asList(DayOfWeek.Monday));

        DateTime actualDateTime = campaign.nearestCycleDate(enrollment);
        assertThat(actualDateTime, is(registrationThuFeb2_2012.dayOfMonth().addToCopy(4)));


    }

}
