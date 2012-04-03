package org.motechproject.ghana.national.domain.care;

import ch.lambdaj.Lambda;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.*;
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
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDate;

public class IPTVaccineCare {

    private Patient patient;
    private LocalDate enrollmentDate;
    private PatientCare patientCareBasedOnHistory;
    private Boolean careComplete;

    private IPTVaccineCare(LocalDate enrollmentDate, PatientCare patientCareBasedOnHistory, Patient patient, Boolean careComplete) {
        this.enrollmentDate = enrollmentDate;
        this.patientCareBasedOnHistory = patientCareBasedOnHistory;
        this.patient = patient;
        this.careComplete = careComplete;
    }

    public static IPTVaccineCare createFrom(Patient patient, LocalDate registrationDate, MRSObservation activePregnancyObservation) {

        PatientCare careFromHistory = null;
        Set<MRSObservation> dependantObservations = nullSafe(activePregnancyObservation.getDependantObservations(), emptySet());
        List<MRSObservation> iptObservationHistory = filter(having(on(MRSObservation.class).getConceptName(), is(Concept.IPT.getName())), dependantObservations);
        Boolean careComplete = false;

        if (isNotEmpty(iptObservationHistory)) {
            // TODO #1425: fix patient Care to set the startMilestone
            Double doseVal = (Double) iptObservationHistory.get(0).getValue();
            IPTDose nextMilestoneToSchedule = getNextOf(IPTDose.byValue((Integer.toString(doseVal.intValue()))));
            careFromHistory = (nextMilestoneToSchedule == null) ? null : new PatientCare(ANC_IPT_VACCINE, newDate(iptObservationHistory.get(0).getDate()), registrationDate, nextMilestoneToSchedule.name(), patient.facilityMetaData());
            careComplete = (careFromHistory == null) ? true : false;
        }
        return new IPTVaccineCare(registrationDate, careFromHistory, patient,careComplete);
    }

    public PatientCare care(LocalDate expectedDeliveryDate) {
        if(careComplete)return null;
        List<PatientCare> historyIPT = Lambda.filter(having(on(PatientCare.class).name(), is(ANC_IPT_VACCINE)), patientCareBasedOnHistory);
        return isNotEmpty(historyIPT) ? historyIPT.get(0) : new PatientCare(ANC_IPT_VACCINE, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception(), enrollmentDate, null,patient.facilityMetaData());
    }
}
