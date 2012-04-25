package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.PNCBabyForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.mapper.PNCBabyRequestMapper;
import org.motechproject.ghana.national.service.ChildVisitService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.service.request.PNCBabyRequest;
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
public class PNCBabyFormHandler implements FormPublishHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ChildVisitService childVisitService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    StaffService staffService;

    @Autowired
    PatientService patientService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.pncBabyRequest")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        PNCBabyForm pncBabyForm = (PNCBabyForm) event.getParameters().get(Constants.FORM_BEAN);
        try {
            PNCBabyRequest pncBabyRequest = createRequest(pncBabyForm);
            childVisitService.save(pncBabyRequest);
        } catch (Exception e) {
            log.error("Exception occured in saving PNC Mother details", e);
            throw new XFormHandlerException(event.getSubject(), e);
        }
    }

    private PNCBabyRequest createRequest(PNCBabyForm pncBabyForm) {
        Facility facility = facilityService.getFacilityByMotechId(pncBabyForm.getFacilityId());
        MRSUser staff = staffService.getUserByEmailIdOrMotechId(pncBabyForm.getStaffId());
        Patient patient = patientService.getPatientByMotechId(pncBabyForm.getMotechId());

        return new PNCBabyRequestMapper().map(pncBabyForm, patient, staff, facility);
    }
}
