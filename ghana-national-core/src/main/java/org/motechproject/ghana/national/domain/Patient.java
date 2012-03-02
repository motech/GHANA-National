package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.vo.ChildCare;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.union;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.today;

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

    public String getFirstName() {
        return mrsPatient.getPerson().getFirstName();
    }

    public String getLastName() {
        return mrsPatient.getPerson().getLastName();
    }

    public List<PatientCare> ancCareProgramsToEnrollOnRegistration(LocalDate expectedDeliveryDate) {
        return nullSafeList(
                new PatientCare(ANC_DELIVERY, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception()),
                iptPatientCareEnrollOnRegistration(expectedDeliveryDate));
    }

    public List<String> ancCareProgramsToUnEnroll() {
        return Arrays.asList(
                ANC_DELIVERY,
                ANC_IPT_VACCINE,
                TT_VACCINATION_VISIT);
    }

    public List<String> cwcCareProgramsToUnEnroll() {
        return Arrays.asList(
                CWC_BCG,
                CWC_MEASLES_VACCINE,
                CWC_PENTA,
                CWC_IPT_VACCINE,
                CWC_YELLOW_FEVER,
                TT_VACCINATION_VISIT);
    }

    public List<String> allCareProgramsToUnEnroll() {
        return new ArrayList<String>(new HashSet<String>(union(ancCareProgramsToUnEnroll(), cwcCareProgramsToUnEnroll())));
    }

    public PatientCare iptPatientCareEnrollOnRegistration(LocalDate expectedDeliveryDate) {
        if (expectedDeliveryDate != null && basedOnDeliveryDate(expectedDeliveryDate).applicableForIPT()) {
            return new PatientCare(ANC_IPT_VACCINE, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception());
        }
        return null;
    }

    public List<PatientCare> cwcCareProgramToEnrollOnRegistration() {
        LocalDate referenceDate = DateUtil.newDate(this.getMrsPatient().getPerson().getDateOfBirth());
        return nullSafeList(
                new PatientCare(CWC_BCG, referenceDate),
                new PatientCare(CWC_YELLOW_FEVER, referenceDate),
                pentaPatientCare(),
                measlesChildCare());
    }

    public PatientCare iptPatientCareEnrollOnVisitAfter19Weeks(LocalDate visitDate) {
        return new PatientCare(ANC_IPT_VACCINE, visitDate);
    }

    public PatientCare pentaPatientCare() {
        LocalDate referenceDate = birthDate();
        if (ChildCare.basedOnBirthDay(referenceDate).applicableForPenta())
            return new PatientCare(CWC_PENTA, referenceDate);
        return new PatientCare(CWC_PENTA, today());
    }

    private LocalDate birthDate() {
        return newDate(this.getMrsPatient().getPerson().getDateOfBirth());
    }

    private PatientCare measlesChildCare() {
        LocalDate birthDate = birthDate();
        return ChildCare.basedOnBirthDay(birthDate).applicableForMeasles()
                ? new PatientCare(CWC_MEASLES_VACCINE, birthDate) : null;
    }
}
