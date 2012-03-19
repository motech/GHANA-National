package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.vo.ChildCare;
import org.motechproject.mrs.model.MRSPatient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.union;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDateTime;

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

    public List<PatientCare> ancCareProgramsToEnrollOnRegistration(LocalDate expectedDeliveryDate, LocalDate enrollmentDate) {
        return nullSafeList(
                new PatientCare(ANC_DELIVERY, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception(), enrollmentDate),
                ancIPTPatientCareEnrollOnRegistration(expectedDeliveryDate, enrollmentDate));
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
                CWC_OPV_0,
                CWC_OPV_OTHERS,
                CWC_IPT_VACCINE,
                CWC_YELLOW_FEVER,
                TT_VACCINATION_VISIT);
    }

    public List<String> allCareProgramsToUnEnroll() {
        return new ArrayList<String>(new HashSet<String>(union(ancCareProgramsToUnEnroll(), cwcCareProgramsToUnEnroll())));
    }

    public List<PatientCare> cwcCareProgramToEnrollOnRegistration(LocalDate enrollmentDate) {
        ChildCare childCare = childCare();
        LocalDate referenceDate = childCare.birthDate();
        return nullSafeList(
                cwcIPTPatientCareEnrollOnRegistration(childCare, enrollmentDate),
                new PatientCare(CWC_BCG, referenceDate, enrollmentDate),
                new PatientCare(CWC_YELLOW_FEVER, referenceDate, enrollmentDate),
                new PatientCare(CWC_OPV_0,referenceDate,enrollmentDate),
                new PatientCare(CWC_OPV_OTHERS,referenceDate,enrollmentDate),
                pentaPatientCare(enrollmentDate),
                measlesChildCare(enrollmentDate));
    }

    public PatientCare ancIPTPatientCareEnrollOnRegistration(LocalDate expectedDeliveryDate, LocalDate enrollmentDate) {
        if (expectedDeliveryDate != null && basedOnDeliveryDate(expectedDeliveryDate).applicableForIPT()) {
            return new PatientCare(ANC_IPT_VACCINE, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception(), enrollmentDate);
        }
        return null;
    }

    private PatientCare cwcIPTPatientCareEnrollOnRegistration(ChildCare childCare, LocalDate enrollmentDate) {
        if (childCare != null && childCare.applicableForIPT()) {
            return new PatientCare(CWC_IPT_VACCINE, childCare.birthDate(), enrollmentDate);
        }
        return null;
    }

    public PatientCare ancIPTPatientCareEnrollOnVisitAfter19Weeks(LocalDate visitDate) {
        return new PatientCare(ANC_IPT_VACCINE, visitDate, visitDate);
    }

    public PatientCare cwcIPTPatientCareEnrollOnVisitAfter14Weeks(LocalDate visitDate) {
        return new PatientCare(CWC_IPT_VACCINE, visitDate, visitDate);
    }

    public PatientCare pentaPatientCare(LocalDate enrollmentDate) {
        ChildCare childCare = childCare();
        if (childCare.applicableForPenta())
            return new PatientCare(CWC_PENTA, childCare.birthDate(), enrollmentDate);
        return new PatientCare(CWC_PENTA, enrollmentDate, enrollmentDate);
    }

    private ChildCare childCare() {
        return ChildCare.basedOnBirthDay(dateOfBirth());
    }

    public DateTime dateOfBirth() {
        return newDateTime(getMrsPatient().getPerson().getDateOfBirth());
    }

    private PatientCare measlesChildCare(LocalDate enrollmentDate) {
        ChildCare childCare = childCare();
        return childCare.applicableForMeasles() ? new PatientCare(CWC_MEASLES_VACCINE, childCare.birthDate(), enrollmentDate) : null;
    }

    public List<PatientCare> pncBabyProgramsToEnrollOnRegistration() {
        List<PatientCare> cares = new ArrayList<PatientCare>();
        ChildCare care = childCare();
        DateTime birthDateTime = care.birthTime();
        for (PNCChildVisit visit : PNCChildVisit.values()) {
            cares.add(new PatientCare(visit.scheduleName(), birthDateTime, birthDateTime));
        }
        return cares;
    }


    public List<PatientCare> pncMotherProgramsToEnrollOnRegistration(DateTime deliveryDateTime) {
        List<PatientCare> cares = new ArrayList<PatientCare>();
        for (PNCMotherVisit visit : PNCMotherVisit.values()) {
            cares.add(new PatientCare(visit.scheduleName(), deliveryDateTime, deliveryDateTime));
        }
        return cares;
    }

    public PatientCare pncProgramToFulfilOnVisit(DateTime visitDateTime, String scheduleName) {
        return new PatientCare(scheduleName, visitDateTime, visitDateTime);
    }

    public String getGender() {
        return getMrsPatient().getPerson().getGender();
    }


    public Integer getAge() {
        return getMrsPatient().getPerson().getAge();
    }

    public String getPhoneNumber() {
        return getMrsPatient().getPerson().attrValue(PatientAttributes.PHONE_NUMBER.getAttribute());
    }
}
