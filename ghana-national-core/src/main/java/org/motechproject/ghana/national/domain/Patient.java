package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.care.PentaVaccineCare;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;
import org.motechproject.ghana.national.vo.ChildCare;
import org.motechproject.mrs.model.MRSPatient;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.collections.CollectionUtils.union;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.tools.Utility.*;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDateTime;

public class Patient {
    public static List<String> ancCarePrograms = unmodifiableList(asList(ANC_DELIVERY, ANC_IPT_VACCINE, TT_VACCINATION));
    public static List<String> cwcCarePrograms = unmodifiableList(asList(CWC_BCG, CWC_MEASLES_VACCINE, CWC_PENTA, CWC_OPV_0,
            CWC_OPV_OTHERS, CWC_IPT_VACCINE, CWC_YELLOW_FEVER, TT_VACCINATION));
    public static final List<String> pncMotherCarePrograms = unmodifiableList(PNCMotherVisit.schedules());
    public static final List<String> pncChildCarePrograms = unmodifiableList(PNCChildVisit.schedules());
    public static final String FACILITY_META = "facilityId";

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

    // Todo : Fix the meta-data for migration
    public PatientCare patientCareWithoutMetaData(String scheduleName, DateTime referenceDate, DateTime enrollmentDate) {
        return new PatientCare(scheduleName, referenceDate, enrollmentDate, null, null);
    }

    public PatientCare ancDeliveryCareOnVisit(LocalDate expectedDeliveryDate, LocalDate visitDate) {
        return new PatientCare(ANC_DELIVERY, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception(), visitDate, null, facilityMetaData());
    }

    public PatientCare ttVaccinePatientCareOnVisit(LocalDate vaccinationDate) {
        return new PatientCare(ScheduleNames.TT_VACCINATION, vaccinationDate, vaccinationDate, null, facilityMetaData());
    }

    public List<String> allCareProgramsToUnEnroll() {
        return new ArrayList<String>(new HashSet<String>(union(ancCarePrograms, cwcCarePrograms)));
    }

    public List<PatientCare> cwcCareProgramToEnrollOnRegistration(LocalDate enrollmentDate, List<CwcCareHistory> historiesCaptured, CWCCareHistoryVO cwcCareHistoryVO, ActiveCareSchedules activeCareSchedules) {
        ChildCare childCare = childCare();
        LocalDate referenceDate = childCare.birthDate();
        return nullSafeList(
                cwcIPTPatientCareEnrollOnRegistration(childCare, enrollmentDate),
                bcgChildCare(enrollmentDate, referenceDate, historiesCaptured),
                yfChildCare(enrollmentDate, referenceDate, historiesCaptured),
                new PatientCare(CWC_OPV_0, referenceDate, enrollmentDate, null, facilityMetaData()),
                new PatientCare(CWC_OPV_OTHERS, referenceDate, enrollmentDate, null, facilityMetaData()),

                new PentaVaccineCare(this, enrollmentDate, activeCareSchedules.hasActivePentaSchedule(),
                        safeToString(cwcCareHistoryVO.getLastPenta()), cwcCareHistoryVO.getLastPentaDate()).careForReg(),

                measlesChildCare(enrollmentDate, historiesCaptured));
    }

    private PatientCare bcgChildCare(LocalDate enrollmentDate, LocalDate referenceDate, List<CwcCareHistory> cwcCareHistories) {
        return cwcCareHistories.contains(CwcCareHistory.BCG) ? null : new PatientCare(CWC_BCG, referenceDate, enrollmentDate, null, facilityMetaData());
    }

    private PatientCare yfChildCare(LocalDate enrollmentDate, LocalDate referenceDate, List<CwcCareHistory> cwcCareHistories) {
        return cwcCareHistories.contains(CwcCareHistory.YF) ? null : new PatientCare(CWC_YELLOW_FEVER, referenceDate, enrollmentDate, null, facilityMetaData());
    }

    private PatientCare cwcIPTPatientCareEnrollOnRegistration(ChildCare childCare, LocalDate enrollmentDate) {
        if (childCare != null && childCare.applicableForIPT()) {
            return new PatientCare(CWC_IPT_VACCINE, childCare.birthDate(), enrollmentDate, null, facilityMetaData());
        }
        return null;
    }

    public PatientCare ancIPTPatientCareEnrollOnVisitAfter19Weeks(LocalDate visitDate) {
        return new PatientCare(ANC_IPT_VACCINE, visitDate, visitDate, null, facilityMetaData());
    }

    public PatientCare cwcIPTPatientCareEnrollOnVisitAfter14Weeks(LocalDate visitDate) {
        return new PatientCare(CWC_IPT_VACCINE, visitDate, visitDate, null, facilityMetaData());
    }

    public PatientCare cwcPentaPatientCareEnrollOnVisitAfter10Weeks(LocalDate visitDate) {
        return new PatientCare(CWC_PENTA, visitDate, visitDate, null, facilityMetaData());
    }

    private ChildCare childCare() {
        return ChildCare.basedOnBirthDay(dateOfBirth());
    }

    public DateTime dateOfBirth() {
        return newDateTime(getMrsPatient().getPerson().getDateOfBirth());
    }

    private PatientCare measlesChildCare(LocalDate enrollmentDate, List<CwcCareHistory> cwcCareHistories) {
        ChildCare childCare = childCare();
        return childCare.applicableForMeasles() && !cwcCareHistories.contains(CwcCareHistory.MEASLES)
                ? new PatientCare(CWC_MEASLES_VACCINE, childCare.birthDate(), enrollmentDate, null, facilityMetaData()) : null;
    }

    public List<PatientCare> pncBabyProgramsToEnrollOnRegistration() {
        List<PatientCare> cares = new ArrayList<PatientCare>();
        ChildCare care = childCare();
        DateTime birthDateTime = care.birthTime();
        for (String scheduleName : pncChildCarePrograms) {
            cares.add(new PatientCare(scheduleName, birthDateTime, birthDateTime, null, facilityMetaData()));
        }
        return cares;
    }

    public List<PatientCare> pncMotherProgramsToEnrollOnRegistration(DateTime deliveryDateTime) {
        List<PatientCare> cares = new ArrayList<PatientCare>();
        for (String scheduleName : pncMotherCarePrograms) {
            cares.add(new PatientCare(scheduleName, deliveryDateTime, deliveryDateTime, null, facilityMetaData()));
        }
        return cares;
    }

    public PatientCare pncProgramToFulfilOnVisit(DateTime visitDateTime, String scheduleName) {
        return new PatientCare(scheduleName, visitDateTime, visitDateTime, null, facilityMetaData());
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

    public Map<String, String> facilityMetaData() {
        Map<String, String> metaData = new HashMap<String, String>();
        metaData.put(FACILITY_META, mrsPatient.getFacility().getId());
        return metaData;
    }
}
