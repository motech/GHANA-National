package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;

import static org.motechproject.util.DateUtil.newDate;

public class IPTVaccine {

    IPTDose iptDose;
    IPTReaction iptReaction;
    Patient givenTo;
    LocalDate vaccinationDate;

    public static IPTVaccine createFromANCVisit(ANCVisit ancVisit) {
        IPTDose dose = IPTDose.byValue(ancVisit.getIptdose());
        return dose != null
                ? new IPTVaccine(newDate(ancVisit.getDate()), ancVisit.getPatient(), dose,
                    IPTReaction.byValue(ancVisit.getIptReactive())) : null;
    }

    public static IPTVaccine createFromCWCVisit(CWCVisit cwcVisit) {
        IPTDose dose = IPTDose.byValue(cwcVisit.getIptidose());
        return dose != null ? new IPTVaccine(newDate(cwcVisit.getDate()), cwcVisit.getPatient(), dose, null) : null;
    }

    public IPTVaccine(LocalDate vaccinationDate, Patient givenTo, IPTDose iptDose, IPTReaction iptReaction) {
        this.vaccinationDate = vaccinationDate;
        this.givenTo = givenTo;
        this.iptDose = iptDose;
        this.iptReaction = iptReaction;
    }

    public Integer getIptDose() {
        return iptDose.value();
    }

    public String getIptMilestone() {
        return iptDose.milestone();
    }

    public IPTReaction getIptReaction() {
        return iptReaction;
    }

    public String getIptReactionConceptName() {
        return iptReaction.concept().getName();
    }

    public Patient getGivenTo() {
        return givenTo;
    }

    public LocalDate getVaccinationDate() {
        return vaccinationDate;
    }
}
