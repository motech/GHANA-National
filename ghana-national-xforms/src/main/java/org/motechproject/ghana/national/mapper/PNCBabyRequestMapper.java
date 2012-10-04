package org.motechproject.ghana.national.mapper;

import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.PNCChildVisit;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.request.PNCBabyRequest;
import org.motechproject.mrs.model.MRSUser;

public class PNCBabyRequestMapper {

    public PNCBabyRequest map(PNCBabyForm pncBabyForm, Patient patient, MRSUser staff, Facility facility) {
        return new PNCBabyRequest()
                .patient(patient)
                .facility(facility)
                .staff(staff)
                .visit(PNCChildVisit.byVisitNumber(pncBabyForm.getVisitNumber()))
                .weight(pncBabyForm.getWeight())
                .temperature(pncBabyForm.getTemperature())
                .location(pncBabyForm.getLocation())
                .house(pncBabyForm.getHouse())
                .community(pncBabyForm.getCommunity())
                .referred(pncBabyForm.getReferred())
                .maleInvolved(pncBabyForm.getMaleInvolved())
                .date(pncBabyForm.getDate())
                .respiration(pncBabyForm.getRespiration())
                .cordConditionNormal(pncBabyForm.getCordConditionNormal())
                .babyConditionGood(pncBabyForm.getBabyConditionGood())
                .bcg(pncBabyForm.getBcg())
                .opv0(pncBabyForm.getOpv0())
                .comments(pncBabyForm.getComments());
    }
}
