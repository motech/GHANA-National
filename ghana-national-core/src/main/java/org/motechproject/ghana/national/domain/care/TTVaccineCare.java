package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.ActiveCareSchedules;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.mrs.model.MRSObservation;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.emptySet;
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
    private MRSObservation activePregnancyObservation;

    public TTVaccineCare(Patient patient, LocalDate enrollmentDate, MRSObservation activePregnancyObservation, ActiveCareSchedules activeTTSchedule) {
        this.enrollmentDate = enrollmentDate;
        this.hasActiveTTSchedule = activeTTSchedule != null ? activeTTSchedule.hasActiveTTSchedule() : false;
        this.activePregnancyObservation = activePregnancyObservation;
        this.patient = patient;
    }

    public PatientCare care() {
        if (hasActiveTTSchedule || isCareProgramComplete()) return null;
        PatientCare scheduleForNextTTDose = createCareFromHistory(patient, activePregnancyObservation);
        return scheduleForNextTTDose != null ? scheduleForNextTTDose : PatientCare.forEnrollmentFromStart(TT_VACCINATION, enrollmentDate, patient.facilityMetaData());
    }

    private PatientCare createCareFromHistory(Patient patient, MRSObservation activePregnancyObservation) {
        MRSObservation lastTTObservation = lastTTObservation(activePregnancyObservation);
        if (lastTTObservation != null) {
            TTVaccineDosage nextMilestoneToSchedule = nextVaccineDose(lastTTObservation);
            return (nextMilestoneToSchedule == null) ? null :
                    PatientCare.forEnrollmentInBetweenProgram(TT_VACCINATION, newDate(lastTTObservation.getDate()), nextMilestoneToSchedule.getScheduleMilestoneName(), patient.facilityMetaData());
        }
        return null;
    }

    private MRSObservation lastTTObservation(MRSObservation activePregnancyObservation) {
        Set<MRSObservation> dependantObservations = nullSafe(activePregnancyObservation.getDependantObservations(), emptySet());
        List<MRSObservation> ttObservationsHistory = filter(having(on(MRSObservation.class).getConceptName(), is(TT.getName())), dependantObservations);
        return Utility.safeFetch(ttObservationsHistory, 1);
    }

    private static TTVaccineDosage nextVaccineDose(MRSObservation lastTTObservation) {
        return getNextOf(TTVaccineDosage.byValue(((Double) lastTTObservation.getValue()).intValue()));
    }

    private boolean isCareProgramComplete() {
        MRSObservation lastTTObservation = lastTTObservation(activePregnancyObservation);
        return (lastTTObservation != null && nextVaccineDose(lastTTObservation) == null);
    }
}
