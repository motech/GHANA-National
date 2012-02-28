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
        IPTDose dose = IPTDose.byValue(ancVisitRequest.getIptdose());
        return dose != null
                ? new IPTVaccine(newDate(ancVisitRequest.getDate()),
                ancVisitRequest.getPatient(),
                dose,
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
