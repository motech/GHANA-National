package org.motechproject.ghana.national.domain;

import java.util.Comparator;

public class SMSTextComparator<T> implements Comparator<String>{

    @Override
    public int compare(String smsText1, String smsText2) {
        return getAlertWindow(smsText1).getOrder().compareTo(getAlertWindow(smsText2).getOrder());
    }

    private AlertWindow getAlertWindow(String smsText) {
        return AlertWindow.valueOf(smsText.split(" ")[0].toUpperCase());
    }
}
