package org.motechproject.ghana.national.domain;

import org.motechproject.ghana.national.domain.care.*;

import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.nullSafeList;

public class CareHistory {
    private PentaVaccineCare pentaVaccineCare;
    private TTVaccineCare ttVaccineCare;
    private IPTVaccineCare iptVaccineCare;
    private IPTiVaccineCare iptiVaccineCare;
    private OPVVaccineCare opvVaccineCare;

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
                safeCareForHistory(pentaVaccineCare),
                safeCareForHistory(opvVaccineCare),
                safeCareForHistory(iptiVaccineCare)
        );
    }

    private PatientCare safeCareForHistory(VaccineCare care) {
        return care != null ? care.careForHistory() : null;
    }

    public static CareHistory forChildCare(ChildVaccineCare... childVaccineCares) {
        CareHistory careHistory = new CareHistory();
        for (ChildVaccineCare childVaccineCare : childVaccineCares) {
            if (childVaccineCare instanceof PentaVaccineCare) {
                careHistory.pentaVaccineCare = (PentaVaccineCare) childVaccineCare;
            }
            if (childVaccineCare instanceof IPTiVaccineCare) {
                careHistory.iptiVaccineCare = (IPTiVaccineCare) childVaccineCare;
            }
            if(childVaccineCare instanceof OPVVaccineCare){
                careHistory.opvVaccineCare = (OPVVaccineCare) childVaccineCare;
            }
        }
        return careHistory;
    }
}
