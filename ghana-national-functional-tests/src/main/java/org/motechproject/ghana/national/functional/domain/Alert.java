package org.motechproject.ghana.national.functional.domain;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.Date;

import static org.motechproject.util.DateUtil.newDate;

public class Alert {
    private WindowName window;
    private Date alertDate;

    public Alert(WindowName window, Date alertDate) {
        this.window = window;
        this.alertDate = alertDate;
    }

    public WindowName getWindow() {
        return window;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public LocalDate getAlertAsLocalDate() {
        return newDate(alertDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alert)) return false;

        Alert alert = (Alert) o;

        if (alertDate != null ? !alertDate.equals(alert.alertDate) : alert.alertDate != null) return false;
        if (window != alert.window) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = window != null ? window.hashCode() : 0;
        result = 31 * result + (alertDate != null ? alertDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "window=" + window +
                ", alertDate=" + alertDate +
                '}';
    }
}
