package org.motechproject.ghana.national.domain;

import org.motechproject.ghana.national.domain.care.IPTVaccineCare;
import org.motechproject.ghana.national.domain.care.PentaVaccineCare;
import org.motechproject.ghana.national.domain.care.TTVaccineCare;
import org.motechproject.ghana.national.domain.care.VaccineCare;

import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.nullSafeList;

public class CareHistory {
    private PentaVaccineCare pentaVaccineCare;
    private TTVaccineCare ttVaccineCare;
    private IPTVaccineCare iptVaccineCare;

    private CareHistory() {
    }

    public static CareHistory forPregnancy(TTVaccineCare ttVaccineCare, IPTVaccineCare iptVaccineCare) {
        CareHistory careHistory = new CareHistory();
        careHistory.ttVaccineCare = ttVaccineCare;
        careHistory.iptVaccineCare = iptVaccineCare;
        return careHistory;
    }

    public List<PatientCare> cares() {
        return nullSafeList(
                safeCareForHistory(ttVaccineCare),
                safeCareForHistory(iptVaccineCare),
                safeCareForHistory(pentaVaccineCare)
        );
    }

    private PatientCare safeCareForHistory(VaccineCare care) {
        return care != null ? care.careForHistory() : null;
    }

    public static CareHistory forChildCare(PentaVaccineCare pentaVaccineCare) {
        CareHistory careHistory = new CareHistory();
        careHistory.pentaVaccineCare = pentaVaccineCare;
        return careHistory;
    }
}
