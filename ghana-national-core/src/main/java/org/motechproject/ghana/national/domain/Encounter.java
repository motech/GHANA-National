package org.motechproject.ghana.national.domain;

import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;

import java.util.Date;
import java.util.Set;

public class Encounter {

    private MRSUser staff;
    private MRSPatient mrsPatient;
    private MRSFacility facility;
    private EncounterType type;
    private Set<MRSObservation> observations;
    private Date date;

    public Encounter(MRSPatient mrsPatient, MRSUser staff, Facility facility, EncounterType type,
                     Date date, Set<MRSObservation> observations) {
        this.mrsPatient = mrsPatient;
        this.staff = staff;
        this.facility = facility.mrsFacility();
        this.type = type;
        this.date = date;
        this.observations = observations;
    }

    public MRSUser getStaff() {
        return staff;
    }

    public MRSPatient getMrsPatient() {
        return mrsPatient;
    }

    public MRSFacility getFacility() {
        return facility;
    }

    public String getType() {
        return type.value();
    }

    public Set<MRSObservation> getObservations() {
        return observations;
    }


    public Date getDate() {
        return date;
    }
}
