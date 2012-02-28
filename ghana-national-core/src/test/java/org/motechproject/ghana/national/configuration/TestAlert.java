package org.motechproject.ghana.national.configuration;

import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.Date;

public class TestAlert {
    private WindowName window;
    private Date alertDate;

    public TestAlert(WindowName window, Date alertDate) {

        this.window = window;
        this.alertDate = alertDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestAlert testAlert = (TestAlert) o;

        if (alertDate != null ? !alertDate.equals(testAlert.alertDate) : testAlert.alertDate != null) return false;
        if (window != testAlert.window) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = window != null ? window.hashCode() : 0;
        result = 31 * result + (alertDate != null ? alertDate.hashCode() : 0);
        return result;
    }
}
