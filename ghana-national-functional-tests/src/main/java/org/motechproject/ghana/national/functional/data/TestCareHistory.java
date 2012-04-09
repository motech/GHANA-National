package org.motechproject.ghana.national.functional.data;

import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.tools.Utility.nullSafeToString;

public class TestCareHistory {
    private String staffId;
    private String facilityId;
    private String motechId;
    private LocalDate date;
    private String addHistory;
    private String lastIPT;
    private LocalDate lastIPTDate;
    private String lastTT;
    private LocalDate lastTTDate;
    private LocalDate bcgDate;
    private String lastOPV;
    private LocalDate lastOPVDate;
    private String lastPenta;
    private LocalDate lastPentaDate;
    private LocalDate measlesDate;
    private LocalDate yellowFeverDate;
    private String lastIPTI;
    private LocalDate lastIPTIDate;
    private LocalDate lastVitaminADate;

    public static TestCareHistory withoutHistory(String motechId) {
        final TestCareHistory careHistory = new TestCareHistory();
        careHistory.facilityId = "13212";
        careHistory.motechId = motechId;
        careHistory.date = DateUtil.newDate(2000, 12, 12);
        careHistory.addHistory = "";
        return careHistory;
    }

    public TestCareHistory withIPT(String dosage, LocalDate lastIPTdate) {
        lastIPT = dosage;
        this.lastIPTDate = lastIPTdate;
        addHistory = addHistory + "IPT";
        return this;
    }

    public TestCareHistory withTT(String dosage, LocalDate lastIPTdate) {
        lastTT = dosage;
        this.lastTTDate = lastIPTdate;
        addHistory = addHistory + "TT";
        return this;
    }


    public static TestCareHistory create() {
        TestCareHistory careHistory = new TestCareHistory();
        careHistory.facilityId = "13212";
        careHistory.date = DateUtil.newDate(2000, 12, 12);
        careHistory.addHistory = "VITA_A IPTI BCG OPV PENTA MEASLES YF IPT TT";
        careHistory.lastIPT = "1";
        careHistory.lastIPTDate = DateUtil.newDate(2000, 12, 12);
        careHistory.lastTT = "2";
        careHistory.lastTTDate = DateUtil.newDate(2000, 12, 11);
        careHistory.bcgDate = DateUtil.newDate(2000, 12, 10);
        careHistory.lastOPV = "0";
        careHistory.lastOPVDate = DateUtil.newDate(2000, 12, 9);
        careHistory.lastPenta = "3";
        careHistory.lastPentaDate = DateUtil.newDate(2000, 12, 8);
        careHistory.measlesDate = DateUtil.newDate(2000, 12, 7);
        careHistory.yellowFeverDate = DateUtil.newDate(2000, 12, 6);
        careHistory.lastIPTI = "1";
        careHistory.lastIPTIDate = DateUtil.newDate(2000, 12, 5);
        careHistory.lastVitaminADate = DateUtil.newDate(2000, 12, 4);
        return careHistory;
    }

    public Map<String, String> forMobile() {
        return new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("date", safe(TestCareHistory.this.date));
            put("motechId", motechId);
            put("addHistory", addHistory);
            put("lastIPT", lastIPT);
            put("lastIPTDate", safe(lastIPTDate));
            put("lastTT", lastTT);
            put("lastTTDate", safe(lastTTDate));
            put("bcgDate", safe(bcgDate));
            put("lastOPV", lastOPV);
            put("lastOPVDate", safe(lastOPVDate));
            put("lastPenta", lastPenta);
            put("lastPentaDate", safe(lastPentaDate));
            put("measlesDate", safe(measlesDate));
            put("yellowFeverDate", safe(yellowFeverDate));
            put("lastIPTI", lastIPTI);
            put("lastIPTIDate", safe(lastIPTIDate));
            put("lastVitaminADate", safe(lastVitaminADate));
        }};
    }

    private String safe(LocalDate date) {
        return nullSafeToString(date, "yyyy-MM-dd");
    }

    public TestCareHistory staffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public TestCareHistory motechId(String motechId) {
        this.motechId = motechId;
        return this;
    }
}
