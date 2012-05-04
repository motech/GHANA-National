package org.motechproject.ghana.national.web;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.messagegateway.domain.MessageRecipientType;
import org.motechproject.ghana.national.messagegateway.domain.NextMondayDispatcher;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping(value = "/sms/test")
public class SMSGatewayTestController {

    @Autowired
    @Qualifier("messageGateway")
    MessageGateway messageGateway;

    @RequestMapping(value = "new", method = RequestMethod.GET)
    @ResponseBody
    public String newStaff(@RequestParam("date") String date,
                           @RequestParam("time") String time,
                           @RequestParam("text") String text,
                           @RequestParam("ph") String ph,
                           @RequestParam("identifier") String identifier,
                           HttpServletRequest servletRequest) throws UnsupportedEncodingException, ParseException {
        final DateTime dateTime = DateUtil.newDateTime(DateUtil.newDate(new SimpleDateFormat("yyyy-MM-dd").parse(date)),
                new Time(Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1])));
        messageGateway.dispatch(SMS.fromText(text, ph, dateTime, new NextMondayDispatcher(), MessageRecipientType.FACILITY), identifier);
        return "done";
    }
}
