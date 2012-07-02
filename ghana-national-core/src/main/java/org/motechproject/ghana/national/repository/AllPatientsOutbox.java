package org.motechproject.ghana.national.repository;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.domain.AlertType;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;

@Repository
public class AllPatientsOutbox {

    public static final String AUDIO_CLIP_NAME = "AUDIO_CLIP_NAME";
    public static final String WINDOW = "WINDOW";
    public static final String WINDOW_START = "WINDOW_START";
    public static final String TYPE = "TYPE";

    @Autowired
    VoiceOutboxService voiceOutboxService;
    @Autowired
    MRSPatientAdapter mrsPatientAdapter;

    public void addAppointmentMessage(String motechId, final String clipName, Period validity) {
        HashMap<String, Object> messageAttributes = new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(TYPE, AlertType.APPOINTMENT);
        }};
        addMessage(motechId, validity, messageAttributes);
    }

    public void addMobileMidwifeMessage(String motechId, final String clipName, Period validity) {
        HashMap<String, Object> messageAttributes = new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(TYPE, AlertType.MOBILE_MIDWIFE);
        }};
        addMessage(motechId, validity, messageAttributes);
    }

    public void addCareMessage(String motechId, final String clipName, Period validity, final AlertWindow window, final DateTime windowStart) {
        HashMap<String, Object> messageAttributes = new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(WINDOW, window);
            put(WINDOW_START, windowStart);
            put(TYPE, AlertType.CARE);
        }};
        addMessage(motechId, validity, messageAttributes);
    }

    private void addMessage(String motechId, Period validity, HashMap<String, Object> messageAttributes) {
        DateTime now = DateTime.now();
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setCreationTime(now.toDate());
        outboundVoiceMessage.setExpirationDate(now.plus(validity).toDate());
        outboundVoiceMessage.setExternalId(motechId);
        outboundVoiceMessage.setParameters(messageAttributes);
        voiceOutboxService.addMessage(outboundVoiceMessage);
    }

    public List<String> getAudioFileNames(String motechId) {
        List<OutboundVoiceMessage> messages = voiceOutboxService.getMessages(motechId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        final List<OutboundVoiceMessage> mmclips = Lambda.filter(having(on(OutboundVoiceMessage.class).getParameters(), hasEntry(TYPE, AlertType.MOBILE_MIDWIFE)), messages);
        final List<OutboundVoiceMessage> appointmentClips = Lambda.filter(having(on(OutboundVoiceMessage.class).getParameters(), hasEntry(TYPE, AlertType.APPOINTMENT)), messages);
        final List<OutboundVoiceMessage> careClips = nullSafeList(Lambda.filter(having(on(OutboundVoiceMessage.class).getParameters(), hasEntry(TYPE, AlertType.CARE)), messages));
        Collections.sort(careClips, new Comparator<OutboundVoiceMessage>() {
            @Override
            public int compare(OutboundVoiceMessage outboundVoiceMessage1, OutboundVoiceMessage outboundVoiceMessage2) {
                AlertWindow messageOneAlertWindow = (AlertWindow) outboundVoiceMessage1.getParameters().get(WINDOW);
                AlertWindow messageTwoAlertWindow = (AlertWindow) outboundVoiceMessage2.getParameters().get(WINDOW);
                DateTime messageOneScheduleStart = (DateTime) outboundVoiceMessage1.getParameters().get(WINDOW_START);
                DateTime messageTwoScheduleStart = (DateTime) outboundVoiceMessage2.getParameters().get(WINDOW_START);

                int windowOrder = messageTwoAlertWindow.getOrder().compareTo(messageOneAlertWindow.getOrder());
                if (windowOrder == 0) {
                    return messageOneScheduleStart.compareTo(messageTwoScheduleStart);
                } else {
                    return windowOrder;
                }
            }
        });

        ArrayList<OutboundVoiceMessage> sortedMessages = new ArrayList<OutboundVoiceMessage>() {{
            addAll(careClips);
            addAll(appointmentClips);
            addAll(mmclips);
        }};

        return convert(sortedMessages, new Converter<OutboundVoiceMessage, String>() {
            @Override
            public String convert(OutboundVoiceMessage outboundVoiceMessage) {
                return (String)outboundVoiceMessage.getParameters().get(AUDIO_CLIP_NAME);
            }
        });
    }
    
    public void removeMobileMidwifeMessages(String motechId){
        List<OutboundVoiceMessage> messages = voiceOutboxService.getMessages(motechId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        for (OutboundVoiceMessage message : messages) {
            if(message.getParameters().get(TYPE).equals(AlertType.MOBILE_MIDWIFE.name()))
                voiceOutboxService.removeMessage(message.getId());
        }
    }
    
    public void removeCareAndAppointmentMessages(String mrsPatientId, String scheduleName){
        String motechId = mrsPatientAdapter.getPatient(mrsPatientId).getMotechId();
        List<OutboundVoiceMessage> messages = voiceOutboxService.getMessages(motechId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        for (OutboundVoiceMessage message : messages) {
            String audio_clip_name = (String) message.getParameters().get(AUDIO_CLIP_NAME);
            if(audio_clip_name.contains(scheduleName))
                voiceOutboxService.removeMessage(message.getId());
        }
    }
}
