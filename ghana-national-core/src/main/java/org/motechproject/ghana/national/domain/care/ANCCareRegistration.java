package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;

import java.util.List;

import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_DELIVERY;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

public class ANCCareRegistration {

    TTVaccineCare ttVaccineCare;
    IPTVaccineCare iptVaccineCare;
    Patient patient;
    private LocalDate expectedDeliveryDate;
    private LocalDate enrollmentDate;

    public ANCCareRegistration(TTVaccineCare ttVaccineCare, IPTVaccineCare iptVaccineCare, Patient patient, LocalDate expectedDeliveryDate, LocalDate enrollmentDate) {
        this.ttVaccineCare = ttVaccineCare;
        this.iptVaccineCare=iptVaccineCare;
        this.patient = patient;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.enrollmentDate = enrollmentDate;
    }

    public List<PatientCare> allCares() {
         return nullSafeList(
                new PatientCare(ANC_DELIVERY, basedOnDeliveryDate(expectedDeliveryDate).dateOfConception(), enrollmentDate, patient.facilityMetaData()),
                ttVaccineCare.care(),
                iptVaccineCare.care(expectedDeliveryDate)
         );
    }
}
