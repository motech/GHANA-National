package org.motechproject.ghana.national.messagegateway.domain;

import ch.lambdaj.group.Group;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ReleaseStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;

@MessageEndpoint
public class MessageDispatcher {

    public static final String SMS_SEPARATOR = "%0a";
    @Autowired
    private MessageGateway messageGateway;

    public SMS aggregateSMS(List<SMS> smsMessages) {
        final SMS firstSMSInTheGroup = smsMessages.get(0);
        List<SMSDatum> smsData = getSMSData(smsMessages);

        Collection<String> windows = selectDistinct(extract(smsData, on(SMSDatum.class).getWindowName()), firstSMSInTheGroup.getComparator());
        Collection<String> motechIds = selectDistinct(extract(smsData, on(SMSDatum.class).getMotechId()), firstSMSInTheGroup.getComparator());
        Group<SMSDatum> groupedByWindow = group(smsData, by(on(SMSDatum.class).getWindowName()), by(on(SMSDatum.class).getMotechId()), by(on(SMSDatum.class).getMilestone()));

        Group<SMSDatum> motechIdSubGroup;
        Group<SMSDatum> subWindowGroup;

        List<String> messages = new ArrayList<String>();
        for (String window : windows) {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(window).append(": ");
            subWindowGroup = groupedByWindow.findGroup(window);
            int count = 0;
            for (String motechId : motechIds) {
                if ((motechIdSubGroup = subWindowGroup.findGroup(motechId)) != null) {
                    count += 1;
                    List<SMSDatum> all = motechIdSubGroup.findAll();
                    SMSDatum datum = all.get(0);
                    if (count != 1) stringBuffer.append(", ");
                    stringBuffer.append(datum.getFirstName()).append(" ")
                            .append(datum.getLastName()).append(", ")
                            .append(datum.getMotechId()).append(", ")
                            .append(datum.getSerialNumber()).append(", ");
                    stringBuffer.append(joinFrom(all).getMilestone());
                }
            }
            messages.add(stringBuffer.toString());
        }

        return SMS.fromText(join(messages, SMS_SEPARATOR), firstSMSInTheGroup.getPhoneNumber(), DateUtil.now(),
                firstSMSInTheGroup.getDeliveryStrategy(), firstSMSInTheGroup.getComparator());
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

    @CorrelationStrategy
    public String correlateByRecipientAndDeliveryDate(SMS sms) {
        return new FLWSMSIdentifier(sms).toString();
    }

    @ReleaseStrategy
    public boolean canBeDispatched(List<SMS> smsMessages) {
        return smsMessages.get(0).canBeDispatched();
    }

    public class SMSDatum {
        private String windowName;
        private String milestone;
        private String firstName;
        private String lastName;
        private String motechId;
        private String serialNumber;

        private SMSDatum(String windowName, String milestone, String motechId, String serialNumber, String firstName, String lastName) {
            this.windowName = windowName;
            this.milestone = milestone;
            this.firstName = firstName;
            this.lastName = lastName;
            this.motechId = motechId;
            this.serialNumber = serialNumber;
        }

        public String getWindowName() {
            return windowName;
        }

        public String getMilestone() {
            return milestone;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getMotechId() {
            return motechId;
        }

        public String getSerialNumber() {
            return serialNumber;
        }
    }
}
