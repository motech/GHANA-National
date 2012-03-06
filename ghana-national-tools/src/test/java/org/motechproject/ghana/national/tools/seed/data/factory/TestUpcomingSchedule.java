package org.motechproject.ghana.national.tools.seed.data.factory;

import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;

public class TestUpcomingSchedule {
    private String dueDatetime;
    private String patientId;
    private String milestoneName;
    private String type;
    private String minDatetime;
    private String lateDatetime;
    private String maxDatetime;
    private Integer voided;

    private TestUpcomingSchedule() {
    }

    public static TestUpcomingSchedule newUpcomingSchedule(String patientId, String dueDate, String milestoneName) {
        TestUpcomingSchedule testUpcomingSchedule = new TestUpcomingSchedule();
        testUpcomingSchedule.patientId = patientId;
        testUpcomingSchedule.dueDatetime = dueDate;
        testUpcomingSchedule.milestoneName = milestoneName;
        testUpcomingSchedule.type = "type";
        testUpcomingSchedule.minDatetime = "2012-9-22 10:30:00.0";
        testUpcomingSchedule.lateDatetime = "2012-9-23 10:30:00.0";
        testUpcomingSchedule.maxDatetime = "2012-9-24 10:30:00.0";
        testUpcomingSchedule.voided = 0;
        return testUpcomingSchedule;
    }

    public UpcomingSchedule build(){
        return new UpcomingSchedule(patientId, dueDatetime, milestoneName, type, minDatetime, lateDatetime, maxDatetime, voided);
    }

    public TestUpcomingSchedule markVoided(){
        this.voided = 1;
        return this;
    }

    public TestUpcomingSchedule lateDateTime(String lateDateTime) {
        this.lateDatetime = lateDateTime;
        return this;
    }

    public TestUpcomingSchedule maxDateTime(String maxDateTime) {
        this.maxDatetime = maxDateTime;
        return this;
    }
}
