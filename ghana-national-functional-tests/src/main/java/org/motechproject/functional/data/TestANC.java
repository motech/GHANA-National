package org.motechproject.functional.data;


import net.sf.cglib.core.Local;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.util.DateUtil;

import java.util.*;

public class TestANC {

    private boolean addHistory;
    private String ancRegNumber;
    private List<String> addMotherHistory;
    private LocalDate expDeliveryDate;
    private Boolean deliveryDateConfirmed;
    private Double height;
    private Integer gravida;
    private Integer parity;
    private LocalDate lastIPTDate;
    private LocalDate lastTTDate;
    private String lastIPT;
    private String lastTT;


    public static TestANC with(String ancRegNumber) {
        TestANC anc = new TestANC();
        anc.ancRegNumber = ancRegNumber;
        anc.expDeliveryDate = DateUtil.newDate(2012, 1, 21);
        anc.deliveryDateConfirmed = true;
        anc.height = 142.5;
        anc.gravida = 2;
        anc.parity = 4;
        anc.addHistory = false;
//        anc.addMotherHistory=new ArrayList<String>(){{
//            add("IPT");
//            add("TT");
//        }};
        anc.lastIPTDate = DateUtil.newDate(2010, 12, 4);
        anc.lastIPT = "IPT 1";
        anc.lastTTDate = DateUtil.newDate(2010, 10, 20);
        anc.lastTT = "TT";
        return anc;
    }

    public Map<String, String> forMobile() {
        return new HashMap<String, String>() {{
            put("ancRegNumber", ancRegNumber);
            put("expDeliveryDate", expDeliveryDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            put("deliveryDateConfirmed", booleanCode(deliveryDateConfirmed));
            put("addHistory", booleanCode(addHistory));
            put("height", height.toString());
            put("gravida", gravida.toString());
            put("parity", parity.toString());
            put("lastIPTDate", lastIPTDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            put("lastTTDate", lastTTDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            put("lastTT", lastTT);
            put("lastIPT", lastIPT);

        }};
    }

    public String lastTT() {
        return lastTT;
    }

    public TestANC lastTT(String lastTT) {
        this.lastTT = lastTT;
        return this;
    }

    public String ancRegNumber() {
        return ancRegNumber;
    }

    public TestANC ancRegNumber(String ancRegNumber) {
        this.ancRegNumber = ancRegNumber;
        return this;
    }

    public LocalDate expDeliveryDate() {
        return expDeliveryDate;
    }

    public TestANC expDeliveryDate(LocalDate expDeliveryDate) {
        this.expDeliveryDate = expDeliveryDate;
        return this;
    }

    public Boolean deliveryDateConfirmed() {
        return deliveryDateConfirmed;
    }

    public TestANC deliveryDateConfirmed(Boolean deliveryDateConfirmed) {
        this.deliveryDateConfirmed = deliveryDateConfirmed;
        return this;
    }

    public Double height() {
        return height;
    }

    public TestANC height(Double height) {
        this.height = height;
        return this;
    }

    public Integer gravida() {
        return gravida;
    }

    public TestANC gravida(Integer gravida) {
        this.gravida = gravida;
        return this;
    }

    public Integer parity() {
        return parity;
    }

    public TestANC parity(Integer parity) {
        this.parity = parity;
        return this;
    }

    public LocalDate lastIPTDate() {
        return lastIPTDate;
    }

    public TestANC lastIPTDate(LocalDate lastIPTDate) {
        this.lastIPTDate = lastIPTDate;
        return this;
    }

    public LocalDate lastTTDate() {
        return lastTTDate;
    }

    public TestANC lastTTDate(LocalDate lastTTDate) {
        this.lastTTDate = lastTTDate;
        return this;
    }

    public String lastIPT() {
        return lastIPT;
    }

    public TestANC lastIPT(String lastIPT) {
        this.lastIPT = lastIPT;
        return this;
    }

    public String booleanCode(boolean value) {
        return value ? "Y" : "N";
    }
}
