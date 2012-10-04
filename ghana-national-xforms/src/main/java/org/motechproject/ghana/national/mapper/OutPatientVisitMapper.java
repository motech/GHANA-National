package org.motechproject.ghana.national.mapper;

import org.motechproject.ghana.national.bean.OutPatientVisitForm;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.motechproject.ghana.national.handlers.OutPatientVisitFormHandler;

public class OutPatientVisitMapper {

    public OutPatientVisit map(OutPatientVisitForm visitForm) {
        OutPatientVisit visit = new OutPatientVisit();
        Integer diagnosis = OutPatientVisitFormHandler.OTHER_DIAGNOSIS.equals(visitForm.getDiagnosis()) ? visitForm.getOtherDiagnosis() : visitForm.getDiagnosis();
        Integer secondaryDiagnosis = null;
        if (visitForm.getSecondDiagnosis() != null) {
            secondaryDiagnosis = OutPatientVisitFormHandler.OTHER_DIAGNOSIS.equals(visitForm.getSecondDiagnosis()) ? visitForm.getOtherSecondaryDiagnosis() : visitForm.getSecondDiagnosis();
        }
        visit.setVisitDate(visitForm.getVisitDate()).setRegistrantType(visitForm.getRegistrantType()).setSerialNumber(visitForm.getSerialNumber()).setFacilityId(visitForm.getFacilityId())
                .setStaffId(visitForm.getStaffId()).setDateOfBirth(visitForm.getDateOfBirth()).setInsured(visitForm.getInsured()).setNhis(visitForm.getNhis()).setNhisExpires(visitForm.getNhisExpires())
                .setDiagnosis(diagnosis).setNewCase(visitForm.getNewCase()).setNewPatient(visitForm.getNewPatient()).setSecondDiagnosis(secondaryDiagnosis).setRdtGiven(visitForm.getRdtGiven()).setRdtPositive(visitForm.getRdtPositive())
                .setActTreated(visitForm.getActTreated()).setReferred(visitForm.getReferred()).setComments(visitForm.getComments()).setGender(visitForm.getGender());
        return visit;
    }
}
