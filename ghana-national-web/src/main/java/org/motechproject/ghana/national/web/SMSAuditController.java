package org.motechproject.ghana.national.web;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.SMSAudit;
import org.motechproject.ghana.national.repository.AllSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

@Controller
public class SMSAuditController {
    @Autowired
    private AllSMS allSMS;

    @RequestMapping("/audit")
    public void getAllAudits(HttpServletResponse response) throws IOException {
        List<SMSAudit> messageAudits = sort(allSMS.getAll(), on(SMSAudit.class).getTimeOfMessage(), sortComparator());
        StringBuilder content = new StringBuilder("<html><body style=\"font-family: \'Arial, Helvetica, sans-serif\'\"><table border=\'1\' cellspacing=\'0\' width=\'80%\' align=\'center\'>");
        content.append(createHeader());
        for (SMSAudit messageAudit : messageAudits) {
            content.append(createRow(messageAudit));
        }
        content.append("</table></body></html>");
        response.getWriter().print(content.toString());
    }

    private String createHeader() {
        return "<tr align=\'center\'> <td colspan=\'3\'> <b>SMS Audits</b> </td> </tr><tr><th width=\'20%\'>Recipient</th><th width=\'20%\'>Date</th><th width=\'60%\'>Message</th></tr>";
    }

    private String createRow(SMSAudit audit) {
        return "<tr><td>" + audit.getRecipient()
                + "</td><td>" + audit.getTimeOfMessage().toDate()
                + "</td><td>" + audit.getMessage() + "</td></tr>";
    }

    private Comparator<DateTime> sortComparator() {
        return new Comparator<DateTime>() {
            @Override
            public int compare(DateTime o1, DateTime o2) {
                return o2.compareTo(o1);
            }
        };
    }
}
