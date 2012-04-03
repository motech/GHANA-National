package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.IPTVaccine;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.TTVaccine;
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

import static org.motechproject.ghana.national.domain.IPTVaccine.createFromANCVisit;
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
        Patient patient = ancVisit.getPatient();
        Set<MRSObservation> eddObservations = allObservations.updateEDD(ancVisit.getEstDeliveryDate(), patient, ancVisit.getStaff().getId());
        if (CollectionUtils.isNotEmpty(eddObservations)) {
            mrsObservations.addAll(eddObservations);
            PatientCare ancDeliveryCare = patient.ancDeliveryCareOnVisit(newDate(ancVisit.getEstDeliveryDate()), newDate(ancVisit.getDate()));
            allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, ancDeliveryCare));
        }
    }

    private void updateANCVisit(ANCVisitRequest ancVisitRequest) {
        allAppointments.fulfillCurrentANCVisit(ancVisitRequest.getPatient(), ancVisitRequest.getDate());
        allAppointments.updateANCVisitSchedule(ancVisitRequest.getPatient(), DateUtil.newDateTime(ancVisitRequest.getNextANCDate()));
    }

    public void enrollOrFulfillScheduleIPTp(IPTVaccine iptVaccine) {
        Patient patient = iptVaccine.getGivenTo();
        LocalDate visitDate = iptVaccine.getVaccinationDate();
        EnrollmentRequest enrollmentOrFulfillRequest = new ScheduleEnrollmentMapper().map(patient, patient.ancIPTPatientCareEnrollOnVisitAfter19Weeks(visitDate).milestoneName(iptVaccine.getIptMilestone()));
        allSchedules.enrollOrFulfill(enrollmentOrFulfillRequest, visitDate);
    }

    public void enrollOrFulfillPNCSchedulesForMother(PNCMotherRequest pncMotherRequest) {
        Patient patient = pncMotherRequest.getPatient();
        allEncounters.persistEncounter(new MotherVisitEncounterFactory().createEncounter(pncMotherRequest));
        PatientCare patientCare = patient.pncProgramToFulfilOnVisit(pncMotherRequest.getDate(), pncMotherRequest.getVisit().scheduleName());
        LocalDate visitDate = pncMotherRequest.getDate().toLocalDate();
        allSchedules.enrollOrFulfill(new ScheduleEnrollmentMapper().map(patient, patientCare), visitDate);
    }
}
