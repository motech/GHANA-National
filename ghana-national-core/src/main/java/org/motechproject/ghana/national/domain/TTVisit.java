package org.motechproject.ghana.national.domain;

import org.motechproject.mrs.model.MRSUser;

import java.util.Date;

public class TTVisit {
    private Date date;
    private Patient patient;
    private MRSUser staff;  
    private Facility facility;
    private TTVaccineDosage dosage;

    public Date getDate() {
        return date;
    }

    public Patient getPatient() {
        return patient;
    }

    public MRSUser getStaff() {
        return staff;
    }

    public Facility getFacility() {
        return facility;
    }

    public TTVaccineDosage getDosage() {
        return dosage;
    }

    public TTVisit dosage(TTVaccineDosage dosage) {
        this.dosage = dosage;
        return this;
    }

    public TTVisit patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public TTVisit staff(MRSUser staff) {
        this.staff = staff;
        return this;
    }

    public TTVisit facility(Facility facility) {
        this.facility = facility;
       return this;
    }

    public TTVisit date(Date date) {
        this.date = date;
        return this;
    }
}
