package org.motechproject.ghana.national.repository;

import org.joda.time.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.AlertType;
import org.motechproject.ghana.national.domain.AlertWindow;
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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.repository.AllPatientsOutbox.*;

public class AllPatientsOutboxTest extends BaseUnitTest {

    @InjectMocks
    AllPatientsOutbox allPatientsOutbox = new AllPatientsOutbox();
    @Mock
    private VoiceOutboxService mockOutboxService;
    private LocalTime currentTime = DateUtil.now().toLocalTime();
    private LocalDate currentDate = DateUtil.today();
    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        dateTime(currentDate, currentTime);
        patient = new Patient(new MRSPatient("motechId", new MRSPerson(), new MRSFacility("facilityId")));
    }

    @Test
    public void shouldAddCareVoiceMessageToOutboxForAGivenExternalId() {
        final String clipName = "clip.wav";
        Date expirationTimeInMillis = DateUtil.newDateTime(new Date(DateTimeUtils.currentTimeMillis())).plus(Period.weeks(1)).toDate();

        final DateTime windowStart = DateUtil.newDateTime(2012, 1, 1);
        allPatientsOutbox.addCareMessage(patient.getMotechId(), clipName, Period.weeks(1), AlertWindow.DUE, windowStart);

        assertMessageAddedToOutbox(expirationTimeInMillis, new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(WINDOW, AlertWindow.DUE);
            put(WINDOW_START, windowStart);
            put(TYPE, AlertType.CARE);
        }});
    }

    private void assertMessageAddedToOutbox(Date expirationTimeInMillis, Map<String, Object> expectedParameters) {
        ArgumentCaptor<OutboundVoiceMessage> messageCaptor = ArgumentCaptor.forClass(OutboundVoiceMessage.class);
        verify(mockOutboxService).addMessage(messageCaptor.capture());

        OutboundVoiceMessage voiceMessage = messageCaptor.getValue();

        assertDate(voiceMessage.getCreationTime(), new Date(DateTime.now().getMillis()));
        assertDate(voiceMessage.getExpirationDate(), expirationTimeInMillis);
        assertThat(voiceMessage.getExternalId(), is(equalTo(patient.getMotechId())));
        assertThat(voiceMessage.getParameters(), is(equalTo(expectedParameters)));
    }

    @Test
    public void shouldAddAppointmentVoiceMessageToOutboxForAGivenExternalId() {
        final String clipName = "clip.wav";
        Date expirationTimeInMillis = DateUtil.newDateTime(new Date(DateTimeUtils.currentTimeMillis())).plus(Period.weeks(1)).toDate();

        allPatientsOutbox.addAppointmentMessage(patient.getMotechId(), clipName, Period.weeks(1));

        assertMessageAddedToOutbox(expirationTimeInMillis, new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(TYPE, AlertType.APPOINTMENT);
        }});

    }

    @Test
    public void shouldAddMobileMidwifeVoiceMessageToOutboxForAGivenExternalId() {
        final String clipName = "clip.wav";
        Date expirationTimeInMillis = DateUtil.newDateTime(new Date(DateTimeUtils.currentTimeMillis())).plus(Period.weeks(1)).toDate();

        allPatientsOutbox.addMobileMidwifeMessage(patient.getMotechId(), clipName, Period.weeks(1));

        assertMessageAddedToOutbox(expirationTimeInMillis, new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(TYPE, AlertType.MOBILE_MIDWIFE);
        }});
    }

    private void assertDate(Date date1, Date date2) {
        assertThat(date1.getYear(), is(equalTo(date2.getYear())));
        assertThat(date1.getMonth(), is(equalTo(date2.getMonth())));
        assertThat(date1.getMonth(), is(equalTo(date2.getMonth())));
        assertThat(date1.getHours(), is(equalTo(date2.getHours())));
        assertThat(date1.getMinutes(), is(equalTo(date2.getMinutes())));
    }

    @Test
    public void shouldGetAllAudioUrlForExternalId() {
        String language = "en";
        String externalId = "externalId";

        List<OutboundVoiceMessage> messages = new ArrayList<OutboundVoiceMessage>() {{
            add(outboxMessage("url1"));
            add(outboxMessage("url2"));
            add(outboxMessage("url3"));
        }};
        when(mockOutboxService.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(messages);

        List<String> audioUrlsFor = allPatientsOutbox.getAudioFileNames(externalId);
        assertThat(audioUrlsFor, is(asList("url1", "url2", "url3")));

        verify(mockOutboxService).getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
    }

    private OutboundVoiceMessage outboxMessage(final String url) {
        final OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setParameters(new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, url);
        }});
        return outboundVoiceMessage;
    }
}
