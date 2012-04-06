package org.motechproject.ghana.national.repository;

import ch.lambdaj.group.Group;
import org.motechproject.ghana.national.messagegateway.domain.AggregationStrategy;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.messagegateway.domain.SMSDatum;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;

public class AggregationStrategyImpl implements AggregationStrategy {

    public static final String SMS_SEPARATOR = "%0A";

    @Override
    public List<SMS> aggregate(List<SMS> smsMessages) {
        final SMS firstSMS = Utility.nullSafe(smsMessages, 0, null);
        final String phoneNumber = firstSMS != null ? firstSMS.getPhoneNumber() : null;

        ArrayList<SMSDatum> smsData = getSMSData(smsMessages);
        Comparator<String> alphabeticalOrder = new Comparator<String>() {

            @Override
            public int compare(String s, String s1) {
                return s.compareTo(s1);
            }
        };
        Collection<String> windows = selectDistinct(extract(smsData, on(SMSDatum.class).getWindowName()), alphabeticalOrder);
        Collection<String> motechIds = selectDistinct(extract(smsData, on(SMSDatum.class).getMotechId()), alphabeticalOrder);
        Group<SMSDatum> groupedByWindow = group(smsData, by(on(SMSDatum.class).getWindowName()),
                by(on(SMSDatum.class).getMotechId()), by(on(SMSDatum.class).getMilestone()));

        Group<SMSDatum> motechIdSubGroup;
        Group<SMSDatum> subWindowGroup;

        List<SMS> messages = new ArrayList<SMS>();
        for (String window : windows) {
            StringBuilder builder = new StringBuilder();
            builder.append(window).append(": ");
            subWindowGroup = groupedByWindow.findGroup(window);
            int count = 0;
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
            messages.add(SMS.fromText(builder.toString(), phoneNumber, DateUtil.now(), null ,null));
        }
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
}
