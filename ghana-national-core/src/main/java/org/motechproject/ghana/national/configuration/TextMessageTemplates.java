package org.motechproject.ghana.national.configuration;

public class TextMessageTemplates {
    public static final String[] REGISTER_SUCCESS_SMS = new String[]{"REGISTER_SUCCESS_SMS_KEY", "Your request for a new MoTeCH ID was successful \n MoTeCHID: ${motechId} \n Name: ${firstName} \n ${lastName}" };
    public static final String[] DELIVERY_NOTIFICATION_SMS = new String[]{"DELIVERY_NOTIFICATION_SMS_KEY", "Your client ${motechId}, ${firstName} ${lastName} has delivered on ${date}"};
    public static final String[] PREGNANCY_ALERT_SMS = new String[]{"PREGNANCY_ALERT_SMS_KEY", "${motechId}, ${edd}"};
}
