package org.motechproject.ghana.national.domain;

import org.motechproject.ghana.national.domain.care.IPTVaccineCare;
import org.motechproject.ghana.national.domain.care.TTVaccineCare;

import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.nullSafeList;

public class CareHistory {
    private TTVaccineCare ttVaccineCare;
    private IPTVaccineCare iptVaccineCare;

    public CareHistory(TTVaccineCare ttVaccineCare, IPTVaccineCare iptVaccineCare) {
        this.ttVaccineCare = ttVaccineCare;
        this.iptVaccineCare = iptVaccineCare;
    }

    public List<PatientCare> cares() {
        return nullSafeList(
                ttVaccineCare.careForHistory(),
                iptVaccineCare.careForHistory()
        );
    }
}
