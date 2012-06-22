package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.care.*;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;
import org.motechproject.ghana.national.vo.ChildCare;
import org.motechproject.mrs.model.MRSPatient;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.collections.CollectionUtils.union;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;
import static org.motechproject.ghana.national.tools.Utility.safeToString;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDateTime;

public class Patient {
    public static List<String> ancCarePrograms = unmodifiableList(asList(ANC_DELIVERY.getName(), ANC_IPT_VACCINE.getName(), TT_VACCINATION.getName()));
    public static List<String> cwcCarePrograms = unmodifiableList(asList(CWC_ROTAVIRUS.getName(),CWC_BCG.getName(), CWC_MEASLES_VACCINE.getName(), CWC_PENTA.getName(), CWC_OPV_0.getName(),
            CWC_OPV_OTHERS.getName(), CWC_IPT_VACCINE.getName(), CWC_YELLOW_FEVER.getName(), TT_VACCINATION.getName()));
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
        return new PatientCare(ANC_DELIVERY.getName(), basedOnDeliveryDate(expectedDeliveryDate).dateOfConception(), visitDate, null, facilityMetaData());
    }

    public PatientCare ttVaccinePatientCareOnVisit(LocalDate vaccinationDate) {
        return new PatientCare(ScheduleNames.TT_VACCINATION.getName(), vaccinationDate, vaccinationDate, null, facilityMetaData());
    }

    public List<String> allCareProgramsToUnEnroll() {
        return new ArrayList<String>(new HashSet<String>(union(ancCarePrograms, cwcCarePrograms)));
    }

    public List<PatientCare> cwcCareProgramToEnrollOnRegistration(LocalDate enrollmentDate, List<CwcCareHistory> historiesCaptured, CWCCareHistoryVO cwcCareHistoryVO, ActiveCareSchedules activeCareSchedules, Date lastPentaDate, Date lastIPTiDate, Date lastOPVDate, Date lastRotavirusDate, Date lastPneumococcalDate) {
        ChildCare childCare = childCare();
        LocalDate referenceDate = childCare.birthDate();
        return nullSafeList(
                bcgChildCare(enrollmentDate, referenceDate, historiesCaptured),
                yfChildCare(enrollmentDate, referenceDate, historiesCaptured),
                measlesChildCare(enrollmentDate, historiesCaptured),
                opv0ChildCare(enrollmentDate, referenceDate, cwcCareHistoryVO),
                opv1ChildCare(enrollmentDate, referenceDate, historiesCaptured, activeCareSchedules, cwcCareHistoryVO, lastOPVDate),
                new PentaVaccineCare(this, enrollmentDate, activeCareSchedules.hasActivePentaSchedule(),
                        safeToString(cwcCareHistoryVO.getLastPenta()), lastPentaDate).careForReg(),
                new RotavirusVaccineCare(this, enrollmentDate, activeCareSchedules.hasActiveRotavirusSchedule(),
                        safeToString(cwcCareHistoryVO.getLastRotavirus()), lastRotavirusDate).careForReg(),
                new PneumococcalVaccineCare(this, enrollmentDate, activeCareSchedules.hasActivePneumococcalSchedule(),
                        safeToString(cwcCareHistoryVO.getLastPneumococcal()), lastPneumococcalDate).careForReg(),
                new IPTiVaccineCare(this, enrollmentDate, activeCareSchedules.hasActiveIPTiSchedule(),
                        safeToString(cwcCareHistoryVO.getLastIPTi()), lastIPTiDate).careForReg()
        );

    }

    public List<PatientCare> cwcCareProgramToEnrollOnHistoryCapture(LocalDate enrollmentDate, List<CwcCareHistory> historiesCaptured, CWCCareHistoryVO cwcCareHistoryVO,
                                                                    ActiveCareSchedules activeCareSchedules, Date lastPentaDate, Date lastIPTiDate, Date lastOPVDate, Date lastRotavirusDate, Date lastPneumococcalDate) {
        ChildCare childCare = childCare();
        LocalDate referenceDate = childCare.birthDate();
        return nullSafeList(
                bcgChildCare(enrollmentDate, referenceDate, historiesCaptured),
                yfChildCare(enrollmentDate, referenceDate, historiesCaptured),
                measlesChildCare(enrollmentDate, historiesCaptured),
                opv0ChildCare(enrollmentDate, referenceDate,cwcCareHistoryVO),
                opv1ChildCare(enrollmentDate, referenceDate, historiesCaptured, activeCareSchedules, cwcCareHistoryVO,lastOPVDate),
                new RotavirusVaccineCare(this, enrollmentDate, activeCareSchedules.hasActiveRotavirusSchedule(),
                        safeToString(cwcCareHistoryVO.getLastRotavirus()), lastRotavirusDate).careForHistory(),
                new PentaVaccineCare(this, enrollmentDate, activeCareSchedules.hasActivePentaSchedule(),
                        safeToString(cwcCareHistoryVO.getLastPenta()), lastPentaDate).careForHistory(),
                new PneumococcalVaccineCare(this, enrollmentDate, activeCareSchedules.hasActivePneumococcalSchedule(),
                        safeToString(cwcCareHistoryVO.getLastPneumococcal()), lastPneumococcalDate).careForHistory(),
                new IPTiVaccineCare(this, enrollmentDate, activeCareSchedules.hasActiveIPTiSchedule(),
                        safeToString(cwcCareHistoryVO.getLastIPTi()), lastIPTiDate).careForHistory()
        );

    }

    private PatientCare opv0ChildCare(LocalDate enrollmentDate, LocalDate referenceDate, CWCCareHistoryVO cwcCareHistoryVO) {
        if(cwcCareHistoryVO.getLastOPV()==null)
            return new PatientCare(CWC_OPV_0.getName(), referenceDate, enrollmentDate, null, facilityMetaData());
        return null;
    }

    private PatientCare bcgChildCare(LocalDate enrollmentDate, LocalDate referenceDate, List<CwcCareHistory> cwcCareHistories) {
        return cwcCareHistories.contains(CwcCareHistory.BCG) ? null : new PatientCare(CWC_BCG.getName(), referenceDate, enrollmentDate, null, facilityMetaData());
    }
    
    private PatientCare opv1ChildCare(LocalDate enrollmentDate, LocalDate referenceDate, List<CwcCareHistory> cwcCareHistories, ActiveCareSchedules activeCareSchedules, CWCCareHistoryVO cwcCareHistoryVO,Date lastOPVDate) {
        if(cwcCareHistories.contains(CwcCareHistory.OPV) && cwcCareHistoryVO.getLastOPV()!= 0)
            return new OPVVaccineCare(this,referenceDate,activeCareSchedules.hasActiveOPVSchedule(),safeToString(cwcCareHistoryVO.getLastOPV()),lastOPVDate,CWC_OPV_OTHERS.getName()).careForHistory();
        else
            return new PatientCare(CWC_OPV_OTHERS.getName(), referenceDate, enrollmentDate, null, facilityMetaData());
    }

    private PatientCare yfChildCare(LocalDate enrollmentDate, LocalDate referenceDate, List<CwcCareHistory> cwcCareHistories) {
        return cwcCareHistories.contains(CwcCareHistory.YF) ? null : new PatientCare(CWC_YELLOW_FEVER.getName(), referenceDate, enrollmentDate, null, facilityMetaData());
    }

    public PatientCare ancIPTPatientCareEnrollOnVisitAfter19Weeks(LocalDate visitDate) {
        return new PatientCare(ANC_IPT_VACCINE.getName(), visitDate, visitDate, null, facilityMetaData());
    }

    public PatientCare cwcIPTPatientCareEnrollOnVisitAfter14Weeks(LocalDate visitDate) {
        return new PatientCare(CWC_IPT_VACCINE.getName(), visitDate, visitDate, null, facilityMetaData());
    }

    public PatientCare cwcPentaPatientCareEnrollOnVisitAfter10Weeks(LocalDate visitDate) {
        return new PatientCare(CWC_PENTA.getName(), visitDate, visitDate, null, facilityMetaData());
    }

    public PatientCare cwcRotavirusPatientCareEnrollOnVisitAfter10Weeks(LocalDate visitDate) {
        return new PatientCare(CWC_ROTAVIRUS.getName(), visitDate, visitDate, null, facilityMetaData());
    }

    public PatientCare cwcPneumococcalPatientCareEnrollOnVisitAfter10Weeks(LocalDate visitDate) {
        return new PatientCare(CWC_PNEUMOCOCCAL.getName(), visitDate, visitDate, null, facilityMetaData());
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
                ? new PatientCare(CWC_MEASLES_VACCINE.getName(), childCare.birthDate(), enrollmentDate, null, facilityMetaData()) : null;
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

    public Map facilityMetaData() {
        Map<String, String> metaData = new HashMap<String, String>();
        metaData.put(FACILITY_META, mrsPatient.getFacility().getId());
        return metaData;
    }

    public PatientCare cwcYellowFeverOnVisit(LocalDate vaccinationDate) {
        return new PatientCare(ScheduleNames.CWC_YELLOW_FEVER.getName(), vaccinationDate, vaccinationDate, null, facilityMetaData());
    }

    public PatientCare cwcMeaslesOnVisit(LocalDate vaccinationDate) {
        return new PatientCare(ScheduleNames.CWC_MEASLES_VACCINE.getName(),vaccinationDate,vaccinationDate,null,facilityMetaData());
    }

    public PatientCare cwcBCGOnVisit(LocalDate vaccinationDate) {
        return new PatientCare(ScheduleNames.CWC_BCG.getName(),vaccinationDate,vaccinationDate,null,facilityMetaData());
    }

    public PatientCare cwcOPVOnVisit(LocalDate vaccinationDate, String opvType) {
        return new PatientCare(opvType,vaccinationDate,vaccinationDate,null,facilityMetaData());
    }


    public String receiveSMSOnPhoneNumber(MobileMidwifeEnrollment mobileMidwifeEnrollment) {
        if (mobileMidwifeEnrollment != null) {
            return mobileMidwifeEnrollment.getPhoneNumber();
        }
        return getPhoneNumber();
    }
}
