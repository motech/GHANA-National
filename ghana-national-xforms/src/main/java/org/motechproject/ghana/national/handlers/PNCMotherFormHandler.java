package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.PNCMotherForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.mapper.PNCMotherRequestMapper;
import org.motechproject.ghana.national.service.*;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PNCMotherFormHandler implements FormPublishHandler {

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

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.pncMotherRequest")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        PNCMotherForm pncMotherForm = (PNCMotherForm) event.getParameters().get(Constants.FORM_BEAN);
        try {
            PNCMotherRequest pncMotherRequest = createRequest(pncMotherForm);
            TTVaccine ttVaccine = TTVaccine.createFromPncMotherRequest(pncMotherRequest);
            if (ttVaccine != null) {
                visitService.createTTSchedule(ttVaccine);
            }
            motherVisitService.enrollOrFulfillPNCSchedulesForMother(createRequest(pncMotherForm));
        } catch (Exception e) {
            log.error("Exception occured in saving PNC Mother details", e);
            throw new XFormHandlerException(event.getSubject(), e);
        }
    }

    private PNCMotherRequest createRequest(PNCMotherForm pncMotherForm) {
        Facility facility = facilityService.getFacilityByMotechId(pncMotherForm.getFacilityId());
        MRSUser staff = staffService.getUserByEmailIdOrMotechId(pncMotherForm.getStaffId());
        Patient patient = patientService.getPatientByMotechId(pncMotherForm.getMotechId());

        return new PNCMotherRequestMapper().map(pncMotherForm, patient, staff, facility);
    }
}
