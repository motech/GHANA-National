package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.IPTDose;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.mrs.model.MRSObservation;

import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.emptySet;
import static org.hamcrest.core.Is.is;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.tools.Utility.getNextOf;
import static org.motechproject.ghana.national.tools.Utility.nullSafe;
import static org.motechproject.util.DateUtil.newDate;

public class IPTVaccineCare {

    private Patient patient;
    private LocalDate expectedDeliveryDate;
    private MRSObservation activePregnancyObservation;

    public IPTVaccineCare(Patient patient, LocalDate expectedDeliveryDate, MRSObservation activePregnancyObservation) {
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.activePregnancyObservation = activePregnancyObservation;
        this.patient = patient;
    }

    public PatientCare care() {
        if(isCareProgramComplete()) return null;
        PatientCare scheduleForNextIPTDose = createCareHistory(patient, lastIPTObservation());
        return scheduleForNextIPTDose != null ? scheduleForNextIPTDose : newEnrollment(expectedDeliveryDate);
    }

    public PatientCare careForHistory() {
        if(isCareProgramComplete()) return null;
        PatientCare scheduleForNextIPTDose = createCareHistory(patient, lastIPTObservation());
        return scheduleForNextIPTDose != null ? scheduleForNextIPTDose : null;
    }

    private  MRSObservation lastIPTObservation() {
        Set<MRSObservation> dependantObservations = nullSafe(activePregnancyObservation.getDependantObservations(), emptySet());
        return Utility.safeFetch(filter(having(on(MRSObservation.class).getConceptName(), is(Concept.IPT.getName())), dependantObservations), 1);
    }

    private boolean isCareProgramComplete() {
        MRSObservation lastIPTObservation = lastIPTObservation();
        return (lastIPTObservation != null && nextVaccineDose(lastIPTObservation) == null);
    }

    private static IPTDose nextVaccineDose(MRSObservation lastIPTObservation) {
        Double doseVal = (Double) lastIPTObservation.getValue();
        return getNextOf(IPTDose.byValue((Integer.toString(doseVal.intValue()))));
    }

    private PatientCare createCareHistory(Patient patient, MRSObservation lastIPTObservation) {

        if (lastIPTObservation != null) {
            IPTDose nextMilestoneToSchedule = nextVaccineDose(lastIPTObservation);
            LocalDate lastIPTDate = newDate(lastIPTObservation.getDate());
            return PatientCare.forEnrollmentInBetweenProgram(ANC_IPT_VACCINE, lastIPTDate, nextMilestoneToSchedule.milestone(), patient.facilityMetaData());
        }
        return null;
    }

    private PatientCare newEnrollment(LocalDate expectedDeliveryDate) {
        Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(expectedDeliveryDate);
        return  pregnancy.applicableForIPT() ?
                PatientCare.forEnrollmentFromStart(ANC_IPT_VACCINE, pregnancy.dateOfConception(), patient.facilityMetaData())
                : null;
    }
}
