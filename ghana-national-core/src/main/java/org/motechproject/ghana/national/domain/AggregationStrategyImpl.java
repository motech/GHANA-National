package org.motechproject.ghana.national.domain;

import ch.lambdaj.group.Group;
import org.apache.commons.collections.CollectionUtils;
import org.motechproject.MotechException;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.messagegateway.domain.AggregationStrategy;
import org.motechproject.ghana.national.messagegateway.domain.MessageRecipientType;
import org.motechproject.ghana.national.messagegateway.domain.Payload;
import org.motechproject.ghana.national.messagegateway.domain.SMSDatum;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.joinFrom;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectDistinct;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static java.util.Arrays.asList;
import static java.util.Locale.getDefault;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.FACILITY;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.WINDOW_NAMES;
import static org.motechproject.ghana.national.domain.AlertWindow.ghanaNationalWindowNames;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;

@Component
public class AggregationStrategyImpl implements AggregationStrategy {

    @Autowired
    CMSLiteService cmsLiteService;

    @Autowired
    PatientService patientService;

    @Autowired
    AllFacilities allFacilities;

    @Autowired
    AllMobileMidwifeEnrollments allMobileMidwifeEnrollments;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @LoginAsAdmin
    @ApiSession
    public List<Payload> aggregate(List<Payload> payloads) {
        List<Payload> smsPayloads = filter(instanceOf(SMSPayload.class), payloads);
        final List<Payload> aggregatedVoicePayloads = filter(instanceOf(VoicePayload.class), payloads);
        final List<Payload> aggregatedSMSPayload = Utility.cast(aggregateSMS(Utility.<Payload, SMSPayload>cast(smsPayloads)));
        return new ArrayList<Payload>(){{addAll(aggregatedSMSPayload); addAll(aggregatedVoicePayloads);}};
    }

    private List<SMSPayload> aggregateSMS(List<SMSPayload> payloads) {
        List<SMSPayload> aggregatedSMSPayload = new ArrayList<SMSPayload>();
        List<SMSPayload> smsPayloadForFacility = filter(having(on(SMSPayload.class).getMessageRecipientType(), equalTo(MessageRecipientType.FACILITY)), payloads);
        List<SMSPayload> smsPayloadForPatient = filter(having(on(SMSPayload.class).getMessageRecipientType(), equalTo(MessageRecipientType.PATIENT)), payloads);
        if (!smsPayloadForFacility.isEmpty()) {
            aggregatedSMSPayload.addAll(processMessagesForFacility(smsPayloadForFacility));
        }
        if (!smsPayloadForPatient.isEmpty()) {
            aggregatedSMSPayload.addAll(nullSafeList(processMessagesForPatient(smsPayloadForPatient)));
        }
        return aggregatedSMSPayload;
    }

    private List<SMSPayload> processMessagesForPatient(List<SMSPayload> smsPayloadForPatient) {
        StringBuilder builder = new StringBuilder();
        for (SMSPayload smsPayload : smsPayloadForPatient) {
            builder.append(smsPayload.getText()).append(Constants.SMS_SEPARATOR);
        }
        SMSPayload smsPayload = SMSPayload.fromText(builder.toString(), smsPayloadForPatient.get(0).getUniqueId(), DateUtil.now(), null, MessageRecipientType.PATIENT);

        Patient patient = patientService.getPatientByMotechId(smsPayload.getUniqueId());
        String patientPhoneNumber = patient.receiveSMSOnPhoneNumber(allMobileMidwifeEnrollments.findActiveBy(smsPayload.getUniqueId()));

        if (patientPhoneNumber != null)
            return asList(SMSPayload.fromPhoneNoAndText(patientPhoneNumber, smsPayload.getText()));
        return new ArrayList<SMSPayload>();
    }

    private List<SMSPayload> processMessagesForFacility(List<SMSPayload> smsPayloadMessages) {
        String standardMessage = SMSPayload.fill(getSMSTemplate(FACILITIES_DEFAULT_MESSAGE_KEY), new HashMap<String, String>() {{
            put(WINDOW_NAMES, join(AlertWindow.ghanaNationalWindowNames(), ", "));
            put(FACILITY, "");
        }});
        List<SMSPayload> filteredMessages = filter(having(on(SMSPayload.class).getText(), not(containsString(standardMessage))), smsPayloadMessages);
        List<SMSPayload> defaultMessagesList = filter(having(on(SMSPayload.class).getText(), containsString(standardMessage)), smsPayloadMessages);
        if(defaultMessagesList.isEmpty()) {
            logger.warn("No facility name to send messages to. returning empty list." + filteredMessages);
            return Collections.emptyList();
        }
        String facilityName = minus(defaultMessagesList.get(0).getText(), standardMessage);
        List<SMSPayload> smsPayloads = (filteredMessages.isEmpty()) ? defaultMessagesList : aggregateMessages(filteredMessages, facilityName);
        List<String> phoneNumbers = allFacilities.getFacilityByMotechId(smsPayloads.get(0).getUniqueId()).getPhoneNumbers();

        List<SMSPayload> smsList = new ArrayList<SMSPayload>();
        for (SMSPayload smsPayload : smsPayloads) {
            for (String phoneNumber : phoneNumbers) {
                smsList.add(SMSPayload.fromPhoneNoAndText(phoneNumber, smsPayload.getText()));
            }
        }
        return smsList;
    }

    private String minus(String string1, String string2) {
        return string1.replace(string2, "").trim();
    }

    private List<SMSPayload> aggregateMessages(List<SMSPayload> smsPayloadMessages, final String facilityName) {
        final SMSPayload firstSMSPayload = Utility.nullSafe(smsPayloadMessages, 0, null);
        final String uniqueId = firstSMSPayload != null ? firstSMSPayload.getUniqueId() : null;
        ArrayList<SMSDatum> smsData = getSMSData(smsPayloadMessages);
        Comparator<String> alphabeticalOrder = new Comparator<String>() {

            @Override
            public int compare(String s, String s1) {
                return s.compareTo(s1);
            }
        };
        Collection<String> motechIds = selectDistinct(extract(smsData, on(SMSDatum.class).getMotechId()), alphabeticalOrder);
        Group<SMSDatum> groupedByWindow = group(smsData, by(on(SMSDatum.class).getWindowName()),
                by(on(SMSDatum.class).getMotechId()), by(on(SMSDatum.class).getMilestone()));

        Group<SMSDatum> motechIdSubGroup;
        Group<SMSDatum> subWindowGroup;

        final List<SMSPayload> messages = new ArrayList<SMSPayload>();
        final List<String> windowsWithoutSMS = new ArrayList<String>();
        for (String window : ghanaNationalWindowNames()) {
            StringBuilder builder = new StringBuilder();
            builder.append(window).append(": ");
            subWindowGroup = groupedByWindow.findGroup(window);
            int count = 0;
            if (subWindowGroup != null) {
                for (String motechId : motechIds) {
                    if ((motechIdSubGroup = subWindowGroup.findGroup(motechId)) != null) {
                        List<SMSDatum> all = motechIdSubGroup.findAll();
                        count += 1;
                        SMSDatum datum = all.get(0);
                        if (count != 1) builder.append(Constants.SMS_LIST_SEPERATOR);
                        builder.append(datum.getFirstName()).append(" ").append(datum.getLastName()).append(", ").append(datum.getMotechId())
                                .append(", ").append(datum.getSerialNumber()).append(", ").append(joinFrom(all).getMilestone());
                    }
                }
            }
            if (count != 0) {
                messages.add(SMSPayload.fromText(builder.toString(), uniqueId, DateUtil.now(), null, MessageRecipientType.FACILITY));
            } else {
                windowsWithoutSMS.add(window);
            }
        }
        if (CollectionUtils.isNotEmpty(windowsWithoutSMS)) {
            messages.add(SMSPayload.fromTemplate(getSMSTemplate(SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY), new HashMap<String, String>() {{
                put(WINDOW_NAMES, join(windowsWithoutSMS, Constants.SMS_LIST_SEPERATOR));
                put(FACILITY, facilityName);
            }}, uniqueId, DateUtil.now(), null, MessageRecipientType.FACILITY));
        }
        return messages;
    }

    private ArrayList<SMSDatum> getSMSData(List<SMSPayload> smsPayloadMessages) {
        List<String> smsList = collect(smsPayloadMessages, on(SMSPayload.class).getText());

        ArrayList<SMSDatum> smsData = new ArrayList<SMSDatum>();
        for (String sms : smsList) {
            String[] strings = sms.split(",");
            if (strings.length == 6) {
                smsData.add(new SMSDatum(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5]));
            }
        }
        return smsData;
    }

    private String getSMSTemplate(String templateKey) {
        try {
            return cmsLiteService.getStringContent(getDefault().getLanguage(), templateKey).getValue();
        } catch (ContentNotFoundException e) {
            throw new MotechException("Encountered exception while aggregating SMS, ", e);
        }
    }
}
