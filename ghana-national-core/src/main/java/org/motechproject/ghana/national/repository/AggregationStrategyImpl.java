package org.motechproject.ghana.national.repository;

import ch.lambdaj.group.Group;
import org.motechproject.ghana.national.messagegateway.domain.AggregationStrategy;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.messagegateway.domain.SMSDatum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.joinFrom;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectDistinct;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;

public class AggregationStrategyImpl implements AggregationStrategy {

    public static final String SMS_SEPARATOR = "%0a";

    @Override
    public String aggregate(List<SMS> smsMessages) {
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

        List<String> messages = new ArrayList<String>();
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
            messages.add(builder.toString());
        }
        return join(messages, SMS_SEPARATOR);
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
