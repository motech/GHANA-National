package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.PregnancyService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PregnancyTerminationFormHandler{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    PregnancyService pregnancyService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    StaffService staffService;

    @Autowired
    PatientService patientService;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(PregnancyTerminationForm pregnancyTerminationForm) {
        try {
        pregnancyService.terminatePregnancy(createPregnancyTerminationVisit(pregnancyTerminationForm));
        }
        catch (Exception e) {
            log.error("Exception occurred while processing pregnancy termination form");
            throw new XFormHandlerException("Exception occurred while processing pregnancy termination form", e);
        }

    }

    PregnancyTerminationRequest createPregnancyTerminationVisit(PregnancyTerminationForm form) {
        Facility facility = facilityService.getFacilityByMotechId(form.getFacilityId());
        MRSUser staff = staffService.getUserByEmailIdOrMotechId(form.getStaffId());
        Patient patient = patientService.getPatientByMotechId(form.getMotechId());

        PregnancyTerminationRequest request = new PregnancyTerminationRequest();
        request.setPatient(patient);
        request.setComments(form.getComments());
        request.setDead(form.getMaternalDeath());
        request.setFacility(facility);
        request.setReferred(form.getReferred());
        request.setStaff(staff);
        request.setTerminationDate(form.getDate());
        request.setTerminationProcedure(form.getProcedure());
        request.setTerminationType(form.getTerminationType());
        request.setComplications(form.getTerminationComplications());
        request.setPostAbortionFPCounselling(form.getPostAbortionFPCounseled());
        request.setPostAbortionFPAccepted(form.getPostAbortionFPAccepted());
        return request;
    }
}
