package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.IPTVaccine;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.factory.MotherVisitEncounterFactory;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.ghana.national.domain.ANCVisit;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mrs.exception.ObservationNotFoundException;
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

    private AllCareSchedules allCareSchedules;
    private AllSchedulesAndMessages allSchedulesAndMessages;
    private AllEncounters allEncounters;
    private AllObservations allObservations;
    private AllAppointments allAppointments;
    private MotherVisitEncounterFactory factory;
    private AllAppointmentsAndMessages allAppointmentsAndMessages;
    private VisitService visitService;

    @Autowired
    public MotherVisitService(AllEncounters allEncounters, AllObservations allObservations, AllCareSchedules allCareSchedules,
                              AllAppointments allAppointments, AllAppointmentsAndMessages allAppointmentsAndMessages, VisitService visitService, AllSchedulesAndMessages allSchedulesAndMessages) {
        this.allEncounters = allEncounters;
        this.allObservations = allObservations;
        this.allAppointments = allAppointments;
        this.allCareSchedules = allCareSchedules;
        this.allAppointmentsAndMessages = allAppointmentsAndMessages;
        this.visitService = visitService;
        this.allSchedulesAndMessages = allSchedulesAndMessages;
        factory = new MotherVisitEncounterFactory();
    }

    public MRSEncounter registerANCVisit(ANCVisit ancVisit) throws ObservationNotFoundException {
        Set<MRSObservation> mrsObservations = factory.createMRSObservations(ancVisit);
        updateEDD(ancVisit, mrsObservations);
        updateIPT(ancVisit, mrsObservations);
        updateTT(ancVisit, mrsObservations);
        updateANCVisit(ancVisit);
        return allEncounters.persistEncounter(factory.createEncounter(ancVisit, mrsObservations));
    }

    protected void updateTT(ANCVisit ancVisit, Set<MRSObservation> mrsObservations) {
        TTVaccine ttVaccine = TTVaccine.createFromANCVisit(ancVisit);
        if (ttVaccine != null) {
            mrsObservations.addAll(factory.createObservationForTT(ttVaccine));
            visitService.createTTSchedule(ttVaccine);
        }
    }

    private void updateIPT(ANCVisit ancVisit, Set<MRSObservation> mrsObservations) {
        IPTVaccine iptVaccine = createFromANCVisit(ancVisit);
        if (iptVaccine != null) {
            mrsObservations.addAll(factory.createObservationsForIPT(iptVaccine));
            enrollOrFulfillScheduleIPTp(iptVaccine);
        }
    }

    private void updateEDD(ANCVisit ancVisit, Set<MRSObservation> mrsObservations) throws ObservationNotFoundException {
        Patient patient = ancVisit.getPatient();
        Set<MRSObservation> eddObservations = allObservations.updateEDD(ancVisit.getEstDeliveryDate(), patient, ancVisit.getStaff().getId(), ancVisit.getDate());
        if (CollectionUtils.isNotEmpty(eddObservations)) {
            mrsObservations.addAll(eddObservations);
            PatientCare ancDeliveryCare = patient.ancDeliveryCareOnVisit(newDate(ancVisit.getEstDeliveryDate()), newDate(ancVisit.getDate()));
            allCareSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, ancDeliveryCare));
        }
    }

    private void updateANCVisit(ANCVisit ancVisit) {
        allAppointmentsAndMessages.fulfillCurrentANCVisit(ancVisit.getPatient(), ancVisit.getDate());
        allAppointments.updateANCVisitSchedule(ancVisit.getPatient(), DateUtil.newDateTime(ancVisit.getNextANCDate()));
    }

    public void enrollOrFulfillScheduleIPTp(IPTVaccine iptVaccine) {
        Patient patient = iptVaccine.getGivenTo();
        LocalDate visitDate = iptVaccine.getVaccinationDate();
        EnrollmentRequest enrollmentOrFulfillRequest = new ScheduleEnrollmentMapper().map(patient, patient.ancIPTPatientCareEnrollOnVisitAfter19Weeks(visitDate).milestoneName(iptVaccine.getIptMilestone()));
        allSchedulesAndMessages.enrollOrFulfill(enrollmentOrFulfillRequest, visitDate);
    }

    public void enrollOrFulfillPNCSchedulesForMother(PNCMotherRequest pncMotherRequest) {
        Patient patient = pncMotherRequest.getPatient();
        allEncounters.persistEncounter(new MotherVisitEncounterFactory().createEncounter(pncMotherRequest));
        PatientCare patientCare = patient.pncProgramToFulfilOnVisit(pncMotherRequest.getDate(), pncMotherRequest.getVisit().scheduleName());
        LocalDate visitDate = pncMotherRequest.getDate().toLocalDate();
        allSchedulesAndMessages.enrollOrFulfill(new ScheduleEnrollmentMapper().map(patient, patientCare), visitDate);
    }
}
