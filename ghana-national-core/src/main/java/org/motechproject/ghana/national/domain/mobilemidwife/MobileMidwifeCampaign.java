package org.motechproject.ghana.national.domain.mobilemidwife;

public class MobileMidwifeCampaign {
    public static String getName(ServiceType serviceType, Medium medium) {
        return serviceType.name() + "_" + medium.name();
    }
}
