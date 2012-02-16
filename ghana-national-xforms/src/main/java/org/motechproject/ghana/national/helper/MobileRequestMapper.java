package org.motechproject.ghana.national.helper;

import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;

public class MobileRequestMapper {
    public static PregnancyTerminationRequest map(PregnancyTerminationForm form) {
        PregnancyTerminationRequest request = new PregnancyTerminationRequest();
        request.setMotechId(form.getMotechId());
        request.setComments(form.getComments());
        request.setDead(form.getMaternalDeath());
        request.setFacilityId(form.getFacilityId());
        request.setReferred(form.getReferred());
        request.setStaffId(form.getStaffId());
        request.setTerminationDate(form.getDate());
        request.setTerminationProcedure(form.getProcedure());
        request.setTerminationType(form.getTerminationType());
        request.setComplications(form.getTerminationComplications());
        request.setPostAbortionFPCounselling(form.getPostAbortionFPCounseled());
        request.setPostAbortionFPAccepted(form.getPostAbortionFPAccepted());
        return request;
    }
}
