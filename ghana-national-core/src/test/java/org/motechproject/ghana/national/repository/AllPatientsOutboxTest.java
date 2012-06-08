package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllPatientsOutboxTest extends BaseUnitTest{

    @InjectMocks
    AllPatientsOutbox allPatientsOutbox = new AllPatientsOutbox();
    @Mock
    private VoiceOutboxService mockOutboxService;
    private LocalTime currentTime = DateUtil.now().toLocalTime();
    private LocalDate currentDate = DateUtil.today();

    @Before
    public void setUp() {
        initMocks(this);
        dateTime(currentDate, currentTime);
    }

    @Test
    public void shouldAddUrlToOutboxForAGivenExternalId() {
        String motechId = "motechId";
        final String clipName = "clip.wav";
        Patient patient = new Patient(new MRSPatient(motechId, new MRSPerson(), new MRSFacility("facilityId")));
        Date expirationTimeInMillis = new Date(DateTimeUtils.currentTimeMillis());
        DateTime expirationTime = DateUtil.newDateTime(expirationTimeInMillis);

        allPatientsOutbox.addAudioClip(patient.getMotechId(), clipName, expirationTime);

        ArgumentCaptor<OutboundVoiceMessage> messageCaptor = ArgumentCaptor.forClass(OutboundVoiceMessage.class);
        verify(mockOutboxService).addMessage(messageCaptor.capture());

        OutboundVoiceMessage voiceMessage = messageCaptor.getValue();

        assertDate(voiceMessage.getCreationTime(), new Date(DateTime.now().getMillis()));
        assertDate(voiceMessage.getExpirationDate(), expirationTimeInMillis);
        assertThat(voiceMessage.getExternalId(), is(equalTo(motechId)));
        Map<String, Object> expectedParameters = new HashMap<String, Object>() {{
            put(AllPatientsOutbox.AUDIO_URL, clipName);
        }};
        assertThat(voiceMessage.getParameters(), is(equalTo(expectedParameters)));
    }

    private void assertDate(Date date1, Date date2){
        assertThat(date1.getYear(), is(equalTo(date2.getYear())));
        assertThat(date1.getMonth(), is(equalTo(date2.getMonth())));
        assertThat(date1.getMonth(), is(equalTo(date2.getMonth())));
        assertThat(date1.getHours(), is(equalTo(date2.getHours())));
        assertThat(date1.getMinutes(), is(equalTo(date2.getMinutes())));
    }

    @Test
    public void shouldGetAllAudioUrlForExternalId(){
        String language = "en";
        String externalId = "externalId";

        List<OutboundVoiceMessage> messages = new ArrayList<OutboundVoiceMessage>() {{
            add(outboxMessage("url1"));
            add(outboxMessage("url2"));
            add(outboxMessage("url3"));
        }};
        when(mockOutboxService.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(messages);

        List<String> audioUrlsFor = allPatientsOutbox.getAudioUrlsFor(externalId, language);
        assertThat(audioUrlsFor, is(asList("url1","url2","url3")));

        verify(mockOutboxService).getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
    }

    private OutboundVoiceMessage outboxMessage(final String url) {
        final OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setParameters(new HashMap<String, Object>() {{
            put("AUDIO_URL", url);
        }});
        return outboundVoiceMessage;
    }
}
