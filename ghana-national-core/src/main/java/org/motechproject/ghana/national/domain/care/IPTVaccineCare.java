package org.motechproject.ghana.national.domain.care;

import ch.lambdaj.Lambda;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.IPTDose;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSObservation;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.emptySet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.core.Is.is;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.ghana.national.tools.Utility.nullSafe;
import static org.motechproject.util.DateUtil.newDate;

public class IPTVaccineCare {

    private Patient patient;
    private LocalDate enrollmentDate;
    private PatientCare patientCareBasedOnHistory;
    private Boolean isCareAlreadyCompleted;

    private IPTVaccineCare(LocalDate enrollmentDate, PatientCare patientCareBasedOnHistory, Patient patient, Boolean isCareAlreadyCompleted) {
        this.enrollmentDate = enrollmentDate;
        this.patientCareBasedOnHistory = patientCareBasedOnHistory;
        this.patient = patient;
        this.isCareAlreadyCompleted = isCareAlreadyCompleted;
    }

    public static IPTVaccineCare createFrom(Patient patient, LocalDate registrationDate, MRSObservation activePregnancyObservation) {

        PatientCare careFromHistory = null;
        Set<MRSObservation> dependantObservations = nullSafe(activePregnancyObservation.getDependantObservations(), emptySet());
        List<MRSObservation> iptObservationHistory = filter(having(on(MRSObservation.class).getConceptName(), is(Concept.IPT.getName())), dependantObservations);
        boolean isProgramAlreadyEnded  = false;

        if (isNotEmpty(iptObservationHistory)) {
            Double doseVal = (Double) iptObservationHistory.get(0).getValue();
            IPTDose nextMilestoneToSchedule = getNextOf(IPTDose.byValue((Integer.toString(doseVal.intValue()))));
            isProgramAlreadyEnded = (nextMilestoneToSchedule == null);

            if(!isProgramAlreadyEnded) {
                LocalDate lastIPTDate = newDate(iptObservationHistory.get(0).getDate());
                careFromHistory = PatientCare.forEnrollmentInBetweenProgram(ANC_IPT_VACCINE, lastIPTDate, nextMilestoneToSchedule.name(), patient.facilityMetaData());
            }
        }
        return new IPTVaccineCare(registrationDate, careFromHistory, patient, isProgramAlreadyEnded);
    }

    public PatientCare care(LocalDate expectedDeliveryDate) {
        if(isCareAlreadyCompleted) return null;
        List<PatientCare> historyIPT = Lambda.filter(having(on(PatientCare.class).name(), is(ANC_IPT_VACCINE)), patientCareBasedOnHistory);
        return isNotEmpty(historyIPT) ? historyIPT.get(0) : newEnrollment(expectedDeliveryDate);
    }

    private PatientCare newEnrollment(LocalDate expectedDeliveryDate) {
        Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(expectedDeliveryDate);
        return  pregnancy.applicableForIPT() ?
                PatientCare.forEnrollmentFromStart(ANC_IPT_VACCINE, pregnancy.dateOfConception(), patient.facilityMetaData())
                : null;
    }
}
