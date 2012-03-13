package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.factory.MotherVisitEncounterFactory;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.lang.Integer.parseInt;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_DELIVERY;
import static org.motechproject.ghana.national.domain.IPTVaccine.createFromANCVisit;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDate;

@Service
public class MotherVisitService {

    private AllSchedules allSchedules;
    private AllEncounters allEncounters;
    private AllObservations allObservations;
    private AllAppointments allAppointments;
    private MotherVisitEncounterFactory factory;
    private VisitService visitService;

    @Autowired
    public MotherVisitService(AllEncounters allEncounters, AllObservations allObservations, AllSchedules allSchedules, AllAppointments allAppointments, VisitService visitService) {
        this.allEncounters = allEncounters;
        this.allObservations = allObservations;
        this.allAppointments = allAppointments;
        this.allSchedules = allSchedules;
        this.visitService = visitService;
        factory = new MotherVisitEncounterFactory();
    }

    public MRSEncounter registerANCVisit(ANCVisitRequest ancVisit) {
        Set<MRSObservation> mrsObservations = factory.createMRSObservations(ancVisit);
        updateEDD(ancVisit, mrsObservations);
        updateIPT(ancVisit, mrsObservations);
        updateTT(ancVisit, mrsObservations);
        updateANCVisit(ancVisit);
        return allEncounters.persistEncounter(factory.createEncounter(ancVisit, mrsObservations));
    }

    protected void updateTT(ANCVisitRequest ancVisit, Set<MRSObservation> mrsObservations) {
        TTVaccine ttVaccine = TTVaccine.createFromANCVisit(ancVisit);
        if (ttVaccine != null) {
            mrsObservations.addAll(factory.createObservationForTT(ttVaccine));
            visitService.createTTSchedule(ttVaccine);
        }
    }

    private void updateIPT(ANCVisitRequest ancVisit, Set<MRSObservation> mrsObservations) {
        IPTVaccine iptVaccine = createFromANCVisit(ancVisit);
        if (iptVaccine != null) {
            mrsObservations.addAll(factory.createObservationsForIPT(iptVaccine));
            enrollOrFulfillScheduleIPTp(iptVaccine);
        }
    }

    private void updateEDD(ANCVisitRequest ancVisit, Set<MRSObservation> mrsObservations) {
        Set<MRSObservation> eddObservations = allObservations.updateEDD(ancVisit.getEstDeliveryDate(), ancVisit.getPatient(), ancVisit.getStaff().getId());
        if (CollectionUtils.isNotEmpty(eddObservations)) {
            mrsObservations.addAll(eddObservations);
            LocalDate conceptionDate = basedOnDeliveryDate(newDate(ancVisit.getEstDeliveryDate())).dateOfConception();
            PatientCare ancDeliveryCare = new PatientCare(ANC_DELIVERY, conceptionDate, newDate(ancVisit.getDate()));
            EnrollmentRequest enrollmentRequest = new ScheduleEnrollmentMapper().map(ancVisit.getPatient(),
                    ancDeliveryCare);
            allSchedules.enroll(enrollmentRequest);
        }
    }

    private void updateANCVisit(ANCVisitRequest ancVisitRequest) {
        allAppointments.fulfillCurrentANCVisit(ancVisitRequest.getPatient(), ancVisitRequest.getDate());
        allAppointments.updateANCVisitSchedule(ancVisitRequest.getPatient(), DateUtil.newDateTime(ancVisitRequest.getNextANCDate()));
    }

    public void enrollOrFulfillScheduleIPTp(IPTVaccine iptVaccine) {
        Patient patient = iptVaccine.getGivenTo();
        LocalDate visitDate = iptVaccine.getVaccinationDate();
        EnrollmentRequest enrollmentOrFulfillRequest = new ScheduleEnrollmentMapper().map(patient, patient.ancIPTPatientCareEnrollOnVisitAfter19Weeks(visitDate), iptVaccine.getIptMilestone());
        allSchedules.enrollOrFulfill(enrollmentOrFulfillRequest, visitDate);
    }

    public void enrollOrFulfillPNCSchedulesForMother(PNCMotherRequest pncMotherRequest) {
        Patient patient = pncMotherRequest.getPatient();
        allEncounters.persistEncounter(new MotherVisitEncounterFactory().createEncounter(pncMotherRequest));
        PNCMotherVisit pncMotherVisit = PNCMotherVisit.byVisitNumber(parseInt(pncMotherRequest.getVisitNumber()));
        PatientCare patientCare = patient.pncMotherProgramToFulfilOnVisit(pncMotherVisit, pncMotherRequest.getDate());
        LocalDate visitDate = pncMotherRequest.getDate().toLocalDate();
        allSchedules.enrollOrFulfill(new ScheduleEnrollmentMapper().map(patient, patientCare), visitDate);
    }
}
