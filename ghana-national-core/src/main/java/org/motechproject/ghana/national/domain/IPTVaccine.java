package org.motechproject.ghana.national.domain;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;

import static org.motechproject.util.DateUtil.newDate;

public class IPTVaccine {

    IPTDose iptDose;
    IPTReaction iptReaction;
    Patient givenTo;
    LocalDate vaccinationDate;

    public static IPTVaccine createFromANCVisit(ANCVisitRequest ancVisitRequest) {
        return ancVisitRequest.getIptdose() != null
                ? new IPTVaccine(newDate(ancVisitRequest.getDate()),
                ancVisitRequest.getPatient(),
                IPTDose.byValue(ancVisitRequest.getIptdose()),
                IPTReaction.byValue(ancVisitRequest.getIptReactive())) : null;
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
