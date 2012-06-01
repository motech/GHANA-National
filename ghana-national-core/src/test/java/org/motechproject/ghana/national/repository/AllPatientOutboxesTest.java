package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllPatientOutboxesTest {

    @InjectMocks
    AllPatientOutboxes allPatientOutboxes = new AllPatientOutboxes();
    @Mock
    private VoiceOutboxService mockOutboxService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldAddUrlToOutboxForAGivenExternalId() {
        String externalId = "externalId";
        String url = "http://someurl:port/contextpath";
        allPatientOutboxes.addUrlToOutbox(url, externalId);

        ArgumentCaptor<OutboundVoiceMessage> messageCaptor = ArgumentCaptor.forClass(OutboundVoiceMessage.class);
        verify(mockOutboxService).addMessage(messageCaptor.capture());

        OutboundVoiceMessage voiceMessage = messageCaptor.getValue();
        assertThat(voiceMessage.getExternalId(), is(externalId));
        assertNotNull(voiceMessage.getParameters().get("AUDIO_URL"));
        assertThat(voiceMessage.getParameters().get("AUDIO_URL").toString(), is(url));
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

        List<String> audioUrlsFor = allPatientOutboxes.getAudioUrlsFor(externalId, language);
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
