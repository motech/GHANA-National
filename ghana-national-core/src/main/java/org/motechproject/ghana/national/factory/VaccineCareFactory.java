package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.care.IPTVaccineCare;
import org.motechproject.ghana.national.domain.care.PentaVaccineCare;
import org.motechproject.ghana.national.domain.care.TTVaccineCare;
import org.motechproject.ghana.national.vo.ANCCareHistoryVO;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;

import java.util.Date;

public class VaccineCareFactory {
    public static TTVaccineCare ttVaccineCare(Patient patient, LocalDate expectedDeliveryDate, LocalDate registrationDate, Boolean hasActiveTTSchedule, ANCCareHistoryVO ancCareHistoryVO) {
        final String lastTT = ancCareHistoryVO.getLastTT();
        final Date lastTTDate = ancCareHistoryVO.getLastTTDate();
        return lastTT != null && lastTTDate != null ? new TTVaccineCare(patient, expectedDeliveryDate, registrationDate, hasActiveTTSchedule, lastTT, lastTTDate) : null;
    }

    public static IPTVaccineCare iptVaccineCare(Patient patient, LocalDate expectedDeliverDate, Boolean hasActiveIPTSchedule, ANCCareHistoryVO ancCareHistoryVO) {

        final Date lastIPTDate = ancCareHistoryVO.getLastIPTDate();
        final String lastIPT = ancCareHistoryVO.getLastIPT();
        return lastIPT != null && lastIPTDate != null ? new IPTVaccineCare(patient, expectedDeliverDate, hasActiveIPTSchedule, lastIPT, lastIPTDate) : null;
    }

    public static PentaVaccineCare pentaVaccineCare(Patient patient, LocalDate enrollmentDate, Boolean hasActivePentaSchedule, CWCCareHistoryVO cwcCareHistoryVO) {
        final Integer lastPenta = cwcCareHistoryVO.getLastPenta();
        final Date lastPentaDate = cwcCareHistoryVO.getLastPentaDate();
        return lastPenta != null && lastPentaDate != null ? new PentaVaccineCare(patient, enrollmentDate, hasActivePentaSchedule, lastPenta.toString(), lastPentaDate) : null;
    }
}
