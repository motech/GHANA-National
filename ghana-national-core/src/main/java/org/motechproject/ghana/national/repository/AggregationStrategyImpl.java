package org.motechproject.ghana.national.repository;

import ch.lambdaj.group.Group;
import org.motechproject.MotechException;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.SmsTemplateKeys;
import org.motechproject.ghana.national.messagegateway.domain.AggregationStrategy;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.messagegateway.domain.SMSDatum;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static java.util.Locale.getDefault;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.WINDOW_NAMES;
import static org.motechproject.ghana.national.domain.AlertWindow.ghanaNationalWindowNames;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY;

public class AggregationStrategyImpl implements AggregationStrategy {

    @Autowired
    private CMSLiteService cmsLiteService;

    @Override
    public List<SMS> aggregate(List<SMS> smsMessages) {
        String defaultMessage = SMS.fill(getSMSTemplate(FACILITIES_DEFAULT_MESSAGE_KEY), new HashMap<String, String>(){{
            put(WINDOW_NAMES, join(AlertWindow.ghanaNationalWindowNames(), ", "));
        }});
        List<SMS> filteredMessages = filter(having(on(SMS.class).getText(), not(equalTo(defaultMessage))), smsMessages);
        return (filteredMessages.isEmpty()) ?
                filter(having(on(SMS.class).getText(), equalTo(defaultMessage)), smsMessages) :
                aggregateMessages(filteredMessages);
    }

    private List<SMS> aggregateMessages(List<SMS> smsMessages) {
        final SMS firstSMS = Utility.nullSafe(smsMessages, 0, null);
        final String phoneNumber = firstSMS != null ? firstSMS.getPhoneNumber() : null;
        ArrayList<SMSDatum> smsData = getSMSData(smsMessages);
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

        final List<SMS> messages = new ArrayList<SMS>();
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
                        if (count != 1) builder.append(", ");
                        builder.append(datum.getFirstName()).append(" ").append(datum.getLastName()).append(", ").append(datum.getMotechId())
                                .append(", ").append(datum.getSerialNumber()).append(", ").append(joinFrom(all).getMilestone());
                    }
                }
            }
            if (count != 0) {
                messages.add(SMS.fromText(builder.toString(), phoneNumber, DateUtil.now(), null, null));
            } else {
                windowsWithoutSMS.add(window);
            }
        }
        messages.add(SMS.fromTemplate(getSMSTemplate(SmsTemplateKeys.FACILITIES_DEFAULT_MESSAGE_KEY), new HashMap<String, String>() {{
            put(WINDOW_NAMES, join(windowsWithoutSMS, ", "));
        }}, phoneNumber, DateUtil.now(), null, null));
        return messages;
    }

    private ArrayList<SMSDatum> getSMSData(List<SMS> smsMessages) {
        List<String> smsList = collect(smsMessages, on(SMS.class).getText());

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
