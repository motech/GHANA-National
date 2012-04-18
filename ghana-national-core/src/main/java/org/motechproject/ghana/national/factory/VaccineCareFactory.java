package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.care.PentaVaccineCare;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;

import java.util.Date;

public class VaccineCareFactory {

    public static PentaVaccineCare pentaVaccineCare(Patient patient, LocalDate enrollmentDate, Boolean hasActivePentaSchedule, CWCCareHistoryVO cwcCareHistoryVO) {
        final Integer lastPenta = cwcCareHistoryVO.getLastPenta();
        final Date lastPentaDate = cwcCareHistoryVO.getLastPentaDate();
        return lastPenta != null && lastPentaDate != null ? new PentaVaccineCare(patient, enrollmentDate, hasActivePentaSchedule, lastPenta.toString(), lastPentaDate) : null;
    }
}
