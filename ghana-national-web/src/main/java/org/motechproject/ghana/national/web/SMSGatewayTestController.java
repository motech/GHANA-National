package org.motechproject.ghana.national.web;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.SMSTextComparator;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/sms/test")
public class SMSGatewayTestController {

    @Autowired
    @Qualifier("messageGateway")
    MessageGateway messageGateway;

    @RequestMapping(value = "new", method = RequestMethod.GET)
    @ResponseBody
    public String newStaff(HttpServletRequest servletRequest) throws UnsupportedEncodingException, ParseException {
        String queryString = URLDecoder.decode(servletRequest.getQueryString(), "UTf-8");
        if(queryString != null){
            final String[] params = queryString.split("&");
            Map<String, String> map = new HashMap<String, String>(){{
                String[] param1 = params[0].split("=");
                String[] param2 = params[1].split("=");
                String[] param3 = params[2].split("=");
                String[] param4 = params[3].split("=");
                put(param1[0], param1[1]);
                put(param2[0], param2[1]);
                put(param3[0], param3[1]);
                put(param4[0], param4[1]);
            }};
            final DateTime dateTime = DateUtil.newDateTime(DateUtil.newDate(new SimpleDateFormat("yyyy-MM-dd").parse(map.get("date"))), new Time(Integer.parseInt(map.get("time").split(":")[0]), Integer.parseInt(map.get("time").split(":")[1])));
            messageGateway.dispatch(SMS.fromText(map.get("text"), map.get("ph"), dateTime.toLocalDateTime(), new NextMondayDispatcher(), new SMSTextComparator<String>()));
        }
        return "done";
    }
}
