package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.CWCVisitForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.repository.AllCWCVisitsForVisitor;
import org.motechproject.ghana.national.service.ChildVisitService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.domain.CWCVisit;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CWCVisitFormHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ChildVisitService childVisitService;
    @Autowired
    FacilityService facilityService;
    @Autowired
    PatientService patientService;
    @Autowired
    StaffService staffService;
    @Autowired
    AllCWCVisitsForVisitor allCWCVisitsForVisitor;

    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(CWCVisitForm cwcVisitForm) {
        try {
            CWCVisit cwcVisit = cwcVisitFor(cwcVisitForm);
            if (!cwcVisit.getVisitor()) {
                childVisitService.save(cwcVisit);
            } else {
                allCWCVisitsForVisitor.add(cwcVisit);
            }
        } catch (Exception e) {
            log.error("Encountered error while processing CWC visit form for patientId:" + cwcVisitForm.getMotechId(), e);
            throw new XFormHandlerException("Encountered error while processing CWC visit form", e);
        }
    }

    private CWCVisit cwcVisitFor(CWCVisitForm form) {
        Facility facility = facilityService.getFacilityByMotechId(form.getFacilityId());
        Patient patient = patientService.getPatientByMotechId(form.getMotechId());
        MRSUser staff = staffService.getUserByEmailIdOrMotechId(form.getStaffId());

        return new CWCVisit().staff(staff).facility(facility).patient(patient)
                .date(form.getDate()).serialNumber(form.getSerialNumber()).weight(form.getWeight())
                .height(form.getHeight()).muac(form.getMuac()).maleInvolved(form.getMaleInvolved())
                .iptidose(form.getIptidose()).pentadose(form.getPentadose()).opvdose(form.getOpvdose())
                .rotavirusdose(form.getRotavirusdose()).pneumococcaldose(form.getPneumococcaldose()).cwcLocation(form.getCwcLocation())
                .comments(form.getComments()).house(form.getHouse()).visitor(form.getVisitor())
                .community(form.getCommunity()).immunizations(form.immunizations())
                .vitaminadose(form.getVitaminadose()).measlesDose(form.getMeaslesdose())
                .growthmonitoringPercentage(form.getGrowthmonitoringPercentage()).growthmonitoringDate(form.getGrowthmonitoringDate());

    }

}
