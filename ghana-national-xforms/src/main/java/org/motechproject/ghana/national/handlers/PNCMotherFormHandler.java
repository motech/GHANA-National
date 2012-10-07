package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.mapper.PNCMotherRequestMapper;
import org.motechproject.ghana.national.service.*;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PNCMotherFormHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private MotherVisitService motherVisitService;
    @Autowired
    private VisitService visitService;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(PNCMotherForm pncMotherForm) {
        try {
            PNCMotherRequest pncMotherRequest = createRequest(pncMotherForm);
            TTVaccine ttVaccine = TTVaccine.createFromPncMotherRequest(pncMotherRequest);
            if (ttVaccine != null) {
                visitService.createTTSchedule(ttVaccine);
            }
            motherVisitService.enrollOrFulfillPNCSchedulesForMother(createRequest(pncMotherForm));
        } catch (Exception e) {
            log.error("Exception occurred while processing PNC Mother form", e);
            throw new XFormHandlerException("Exception occurred while processing PNC Mother form", e);
        }
    }

    private PNCMotherRequest createRequest(PNCMotherForm pncMotherForm) {
        Facility facility = facilityService.getFacilityByMotechId(pncMotherForm.getFacilityId());
        MRSUser staff = staffService.getUserByEmailIdOrMotechId(pncMotherForm.getStaffId());
        Patient patient = patientService.getPatientByMotechId(pncMotherForm.getMotechId());

        return new PNCMotherRequestMapper().map(pncMotherForm, patient, staff, facility);
    }
}
