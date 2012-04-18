package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.SmsTemplateKeys;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregationStrategyImplTest extends BaseUnitTest {

    @Mock
    CMSLiteService mockCmsLiteService;

    @InjectMocks
    AggregationStrategyImpl aggregationStrategy = new AggregationStrategyImpl();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        super.mockCurrentDate(DateUtil.now());
    }

    @Test
    public void shouldFilterDefaultMessagesFromListOfSmsIfThereAreValidMessages() throws ContentNotFoundException {
        final String defaultMessage = "default-message";

        when(mockCmsLiteService.getStringContent(Locale.getDefault().getLanguage(),
                SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY)).thenReturn(new StringContent(null, null, defaultMessage));
        final Comparator<String> alphabeticalOrder = new Comparator<String>() {
            @Override
            public int compare(String s, String s1) {
                return s.compareTo(s1);
            }
        };
        final SMS defaultSMS = SMS.fromText(defaultMessage, "ph", null, null, alphabeticalOrder);
        List<SMS> messagesList = new ArrayList<SMS>() {{
            add(SMS.fromText("window1,milestoneName1,motechId,serialNumber,firstName,lastName", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("window1,milestoneName2,motechId,serialNumber,firstName,lastName", "ph", null, null, alphabeticalOrder));
            add(defaultSMS);
        }};
        List<SMS> filteredSMSs = aggregationStrategy.aggregate(messagesList);
        assertThat(filteredSMSs, not(hasItem(defaultSMS)));
    }

    @Test
    public void shouldNotFilterDefaultMessagesFromListOfSmsIfThereAreNoValidMessages() throws ContentNotFoundException {
        final String defaultMessage = "default-message";

        when(mockCmsLiteService.getStringContent(Locale.getDefault().getLanguage(),
                SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY)).thenReturn(new StringContent(null, null, defaultMessage));
        final Comparator<String> alphabeticalOrder = new Comparator<String>() {
            @Override
            public int compare(String s, String s1) {
                return s.compareTo(s1);
            }
        };
        final SMS defaultSMS = SMS.fromText(defaultMessage, "ph", null, null, alphabeticalOrder);
        List<SMS> messagesList = new ArrayList<SMS>() {{
            add(defaultSMS);
        }};
        List<SMS> filteredSMSs = aggregationStrategy.aggregate(messagesList);
        assertThat(filteredSMSs, hasItem(defaultSMS));
    }

    @Test
    public void shouldAggregateManySMSBasedOnWindowNames() throws ContentNotFoundException {
        final String defaultMessage = "default-message";
        when(mockCmsLiteService.getStringContent(Locale.getDefault().getLanguage(),
                SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY)).thenReturn(new StringContent(null, null, defaultMessage));
        List<SMS> messagesList = new ArrayList<SMS>() {{
            Comparator<String> alphabeticalOrder = new Comparator<String>() {
                @Override
                public int compare(String s, String s1) {
                    return s.compareTo(s1);
                }
            };

            add(SMS.fromText("window1,milestoneName1,motechId,serialNumber,firstName,lastName", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("window1,milestoneName2,motechId,serialNumber,firstName,lastName", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("window2,milestoneName,motechId,serialNumber,firstName,lastName", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("window2,milestoneName,motechId2,serialNumber,firstName2,lastName3", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("window2,milestoneName,motechId3,serialNumber,firstName2,lastName3", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("window3,milestoneName,motechId,serialNumber,firstName,lastName", "ph", null, null, alphabeticalOrder));
        }};

        final List<SMS> aggregatedSMSList = aggregationStrategy.aggregate(messagesList);
        assertThat(aggregatedSMSList, hasItem(SMS.fromText("window1: firstName lastName, motechId, serialNumber, milestoneName1, milestoneName2", "ph", DateUtil.now(), null, null)));
        assertThat(aggregatedSMSList, hasItem(SMS.fromText("window2: firstName lastName, motechId, serialNumber, milestoneName, firstName2 lastName3, motechId2, serialNumber, milestoneName, firstName2 lastName3, motechId3, serialNumber, milestoneName", "ph", DateUtil.now(), null, null)));
        assertThat(aggregatedSMSList, hasItem(SMS.fromText("window3: firstName lastName, motechId, serialNumber, milestoneName", "ph", DateUtil.now(), null, null)));
    }
}
