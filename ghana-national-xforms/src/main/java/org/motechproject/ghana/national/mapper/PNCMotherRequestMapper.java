package org.motechproject.ghana.national.mapper;

import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.PNCMotherVisit;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mrs.model.MRSUser;

public class PNCMotherRequestMapper {
    public PNCMotherRequest map(PNCMotherForm pncMotherForm, Patient patient, MRSUser staff, Facility facility) {
        return new PNCMotherRequest()
                .patient(patient)
                .facility(facility)
                .staff(staff)
                .date(pncMotherForm.getDate())
                .visit(PNCMotherVisit.byVisitNumber(pncMotherForm.getVisitNumber()))
                .vitaminA(pncMotherForm.getVitaminA())
                .ttDose(pncMotherForm.getTtDose())
                .lochiaColour(pncMotherForm.getLochiaColour())
                .lochiaOdourFoul(pncMotherForm.getLochiaOdourFoul())
                .lochiaAmountExcess(pncMotherForm.getLochiaAmountExcess())
                .location(pncMotherForm.getLocation())
                .house(pncMotherForm.getHouse())
                .community(pncMotherForm.getCommunity())
                .referred(pncMotherForm.getReferred())
                .maleInvolved(pncMotherForm.getMaleInvolved())
                .temperature(pncMotherForm.getTemperature())
                .fht(pncMotherForm.getFht())
                .comments(pncMotherForm.getComments());
    }
}
