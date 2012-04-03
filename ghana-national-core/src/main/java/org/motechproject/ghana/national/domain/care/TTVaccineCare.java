package org.motechproject.ghana.national.domain.care;

import ch.lambdaj.Lambda;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.ActiveCareSchedules;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.mrs.model.MRSObservation;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.emptySet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.core.Is.is;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.ghana.national.domain.Concept.TT;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.ghana.national.tools.Utility.nullSafe;
import static org.motechproject.util.DateUtil.newDate;

public class TTVaccineCare {
    private Patient patient;
    private LocalDate enrollmentDate;
    private Boolean hasActiveTTSchedule;
    private PatientCare patientCareBasedOnHistory;
    private Boolean careComplete;

    private TTVaccineCare(LocalDate enrollmentDate, Boolean hasActiveTTSchedule, PatientCare patientCareBasedOnHistory, Patient patient, Boolean careComplete) {
        this.enrollmentDate = enrollmentDate;
        this.hasActiveTTSchedule = hasActiveTTSchedule;
        this.patientCareBasedOnHistory = patientCareBasedOnHistory;
        this.patient = patient;
        this.careComplete = careComplete;
    }

    public static TTVaccineCare createFrom(Patient patient, LocalDate registrationDate, MRSObservation activePregnancyObservation, ActiveCareSchedules activeTTSchedules) {

        PatientCare careFromHistory = null;
        Boolean careComplete=false;
        Set<MRSObservation> dependantObservations = nullSafe(activePregnancyObservation.getDependantObservations(), emptySet());
        List<MRSObservation> ttObservationHistory = filter(having(on(MRSObservation.class).getConceptName(), is(TT.getName())), dependantObservations);

        if (isNotEmpty(ttObservationHistory)) {
            TTVaccineDosage nextMilestoneToSchedule = getNextOf(TTVaccineDosage.byValue(((Double) ttObservationHistory.get(0).getValue()).intValue()));
            careFromHistory = (nextMilestoneToSchedule == null) ? null :
                    new PatientCare(TT_VACCINATION, newDate(ttObservationHistory.get(0).getDate()), registrationDate, nextMilestoneToSchedule.name(), patient.facilityMetaData());
            careComplete = (careFromHistory == null) ? true : false;

        }
        return new TTVaccineCare(registrationDate, activeTTSchedules.hasActiveTTSchedule(), careFromHistory, patient,careComplete);
    }

    public PatientCare care() {
        if (hasActiveTTSchedule) return null;
        if(careComplete) return null;
        List<PatientCare> historyTT = Lambda.filter(having(on(PatientCare.class).name(), is(TT_VACCINATION)), patientCareBasedOnHistory);
        return isNotEmpty(historyTT) ? historyTT.get(0) : new PatientCare(TT_VACCINATION, enrollmentDate, enrollmentDate, null, patient.facilityMetaData());
    }
}
