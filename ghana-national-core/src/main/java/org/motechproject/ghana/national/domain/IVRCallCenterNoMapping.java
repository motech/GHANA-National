package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;

@TypeDiscriminator("doc.type === 'IVRCallCenterNoMapping'")
public class IVRCallCenterNoMapping extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "IVRCallCenterNoMapping";
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private DayOfWeek dayOfWeek;
    @JsonProperty
    private Language language;
    @JsonProperty
    private Time startTime;
    @JsonProperty
    private Time endTime;
    @JsonProperty
    private boolean nurseLine;

    public IVRCallCenterNoMapping() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public Language getLanguage() {
        return language;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public IVRCallCenterNoMapping phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public IVRCallCenterNoMapping dayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public IVRCallCenterNoMapping language(Language language) {
        this.language = language;
        return this;
    }

    public IVRCallCenterNoMapping startTime(Time startTime) {
        this.startTime = startTime;
        return this;
    }

    public IVRCallCenterNoMapping endTime(Time endTime) {
        this.endTime = endTime;
        return this;
    }

    public boolean isNurseLine() {
        return nurseLine;
    }

    public IVRCallCenterNoMapping nurseLine(boolean b) {
        this.nurseLine = b;
        return this;
    }
}
