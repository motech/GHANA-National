package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.vo.ANCVisit;

import static org.motechproject.util.DateUtil.newDate;

public class IPTVaccine {

    IPTDose iptDose;
    IPTReaction iptReaction;
    Patient givenTo;
    LocalDate vaccinationDate;

    public static IPTVaccine createFromANCVisit(ANCVisit ancVisit) {
        return ancVisit.getIptdose() != null
                ? new IPTVaccine(newDate(ancVisit.getDate()),
                ancVisit.getPatient(),
                IPTDose.byValue(ancVisit.getIptdose()),
                IPTReaction.byValue(ancVisit.getIptReactive())) : null;
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
