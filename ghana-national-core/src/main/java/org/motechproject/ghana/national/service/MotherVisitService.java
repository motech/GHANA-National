package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.factory.MotherVisitEncounterFactory;
import org.motechproject.ghana.national.factory.TTVaccinationVisitEncounterFactory;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.mapper.TTVaccinationEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.DELIVERY;
import static org.motechproject.ghana.national.domain.IPTVaccine.createFromANCVisit;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

@Service
public class MotherVisitService {

    private AllEncounters allEncounters;
    private AllObservations allObservations;
    private AllSchedules allSchedules;

    @Autowired
    public MotherVisitService(AllEncounters allEncounters, AllObservations allObservations, AllSchedules allSchedules) {
        this.allEncounters = allEncounters;
        this.allObservations = allObservations;
        this.allSchedules = allSchedules;
    }

    public MRSEncounter registerANCVisit(ANCVisit ancVisit) {
        MotherVisitEncounterFactory factory = new MotherVisitEncounterFactory();
        IPTVaccine iptVaccine = createFromANCVisit(ancVisit);
        Set<MRSObservation> mrsObservations = factory.createMRSObservations(ancVisit);
        Set<MRSObservation> eddObservations = allObservations.updateEDD(ancVisit.getEstDeliveryDate(), ancVisit.getPatient(), ancVisit.getStaff().getId());
        if (CollectionUtils.isNotEmpty(eddObservations)) {
            mrsObservations.addAll(eddObservations);
            createEDDScheduleForANCVisit(ancVisit.getPatient(), ancVisit.getEstDeliveryDate());
        }
        if (iptVaccine != null) {
            mrsObservations.addAll(factory.createObservationsForIPT(iptVaccine));
            createIPTpSchedule(iptVaccine);
        }
        return allEncounters.persistEncounter(factory.createEncounter(ancVisit, mrsObservations));
    }

    public void receivedTT(final TTVaccineDosage dosage, Patient patient, MRSUser staff, Facility facility, final LocalDate vaccinationDate) {
        TTVisit ttVisit = new TTVisit().dosage(dosage).facility(facility).patient(patient).staff(staff).date(vaccinationDate.toDate());
        Encounter encounter = new TTVaccinationVisitEncounterFactory().createEncounterForVisit(ttVisit);
        allEncounters.persistEncounter(encounter);
        final EnrollmentRequest enrollmentRequest = new TTVaccinationEnrollmentMapper().map(patient, vaccinationDate, dosage.getScheduleMilestoneName());
        allSchedules.enrollOrFulfill(enrollmentRequest);
    }

    private void createEDDScheduleForANCVisit(Patient patient, Date estimatedDateOfDelivery) {
        EnrollmentRequest enrollmentRequest = new ScheduleEnrollmentMapper().map(patient,
                new PatientCare(DELIVERY, basedOnDeliveryDate(DateUtil.newDate(estimatedDateOfDelivery)).dateOfConception()));
        allSchedules.enroll(enrollmentRequest);
    }

    private void createIPTpSchedule(IPTVaccine iptVaccine) {
        Patient patient = iptVaccine.getGivenTo();
        LocalDate expectedDeliveryDate = fetchLatestEDD(patient);

        EnrollmentResponse enrollmentResponse = allSchedules.enrollment(queryEnrollmentRequest(patient, ANC_IPT_VACCINE));
        if(enrollmentResponse == null) {
            allSchedules.enroll(enrollmentRequest(patient, patient.iptPatientCareEnrollOnRegistration(expectedDeliveryDate)));
        }
        allSchedules.fulfilCurrentMilestone(enrollmentRequest(patient, patient.iptPatientCareVisit()));
    }

    private LocalDate fetchLatestEDD(Patient patient) {
        MRSObservation eddObservation = allObservations.findObservation(patient.getMRSPatientId(), Concept.EDD.getName());
        return new LocalDate(eddObservation.getValue());
    }

    private EnrollmentRequest enrollmentRequest(Patient patient, PatientCare patientCare) {
        return new ScheduleEnrollmentMapper().map(patient, patientCare);
    }

    private EnrollmentRequest queryEnrollmentRequest(Patient patient, String programName) {
        return new ScheduleEnrollmentMapper().map(patient.getMRSPatientId(), programName);
    }
}