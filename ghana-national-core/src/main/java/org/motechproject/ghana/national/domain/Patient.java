package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import java.util.List;

import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

public class Patient {
    private MRSPatient mrsPatient;

    private String parentId;

    public Patient() {
    }

    public Patient(MRSPatient mrsPatient) {
        this.mrsPatient = mrsPatient;
    }

    public Patient(MRSPatient mrsPatient, String parentId) {
        this.mrsPatient = mrsPatient;
        this.parentId = parentId;
    }

    public MRSPatient getMrsPatient() {
        return mrsPatient;
    }

    public String getParentId() {
        return parentId;
    }

    public Patient parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }
    
    public String getMRSPatientId() {
        return mrsPatient.getId();
    }

    public String getMotechId() {
        return mrsPatient.getMotechId();
    }

    public String getFirstName(){
        return mrsPatient.getPerson().getFirstName();
    }

    public String getLastName() {
        return mrsPatient.getPerson().getLastName();
    }

    public List<PatientCare> ancCareProgramsToEnrollOnRegistration(LocalDate expectedDeliveryDate) {
        return nullSafeList(
                new PatientCare(DELIVERY, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception()),
                iptPatientCareEnrollOnRegistration(expectedDeliveryDate));
    }

    public PatientCare iptPatientCareEnrollOnRegistration(LocalDate expectedDeliveryDate) {
        if (expectedDeliveryDate != null && basedOnDeliveryDate(expectedDeliveryDate).applicableForIPT()) {
            return iptPatientCareEnroll(expectedDeliveryDate);
        }
        return null;
    }
    
     public List<PatientCare> cwcCareProgramToEnrollOnRegistration() {
        return nullSafeList(new PatientCare(BCG, DateUtil.newDate(this.getMrsPatient().getPerson().getDateOfBirth())));
    }

    public PatientCare iptPatientCareEnroll(LocalDate expectedDeliveryDate) {
        return new PatientCare(ANC_IPT_VACCINE, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception());
    }

    public PatientCare iptPatientCareVisit() {
        return new PatientCare(ANC_IPT_VACCINE, DateUtil.today());
    }
}
