package org.motechproject.ghana.national.service.request;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.mrs.model.MRSUser;

import java.util.ArrayList;
import java.util.List;

public class PregnancyDeliveryRequest {

    private MRSUser staff;
    private Patient patient;
    private Facility facility;
    private DateTime deliveryDateTime;
    private ChildDeliveryMode childDeliveryMode;
    private ChildDeliveryOutcome childDeliveryOutcome;
    private Boolean maleInvolved;
    private ChildDeliveryLocation childDeliveryLocation;
    private ChildDeliveredBy childDeliveredBy;
    private DeliveryComplications deliveryComplications;
    private VVF vvf;
    private Boolean maternalDeath;
    private String comments;
    private List<DeliveredChildRequest> deliveredChildRequests = new ArrayList<DeliveredChildRequest>();

    private String sender;

    public MRSUser getStaff() {
        return staff;
    }

    public Patient getPatient() {
        return patient;
    }

    public Facility getFacility() {
        return facility;
    }

    public DateTime getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public ChildDeliveryMode getChildDeliveryMode() {
        return childDeliveryMode;
    }

    public List<DeliveredChildRequest> getDeliveredChildRequests() {
        return deliveredChildRequests;
    }

    public PregnancyDeliveryRequest deliveredChildRequests(List<DeliveredChildRequest> deliveredChildRequests) {
        this.deliveredChildRequests = deliveredChildRequests;
        return this;
    }

    public PregnancyDeliveryRequest addDeliveredChildRequest(DeliveredChildRequest deliveredChildRequest) {
        if(getDeliveredChildRequests() == null) {
            this.deliveredChildRequests = new ArrayList<DeliveredChildRequest>();
        }
        deliveredChildRequests.add(deliveredChildRequest);
        return this;
    }


    public ChildDeliveryOutcome getChildDeliveryOutcome() {
        return childDeliveryOutcome;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public ChildDeliveryLocation getChildDeliveryLocation() {
        return childDeliveryLocation;
    }

    public ChildDeliveredBy getChildDeliveredBy() {
        return childDeliveredBy;
    }

    public DeliveryComplications getDeliveryComplications() {
        return deliveryComplications;
    }

    public VVF getVvf() {
        return vvf;
    }

    public Boolean getMaternalDeath() {
        return maternalDeath;
    }

    public String getComments() {
        return comments;
    }


    public String getSender() {
        return sender;
    }

    public PregnancyDeliveryRequest staff(MRSUser staff) {
        this.staff = staff;
        return this;
    }

    public PregnancyDeliveryRequest patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public PregnancyDeliveryRequest facility(Facility facility) {
        this.facility = facility;
        return this;
    }

    public PregnancyDeliveryRequest deliveryDateTime(DateTime deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
        return this;
    }

    public PregnancyDeliveryRequest childDeliveryMode(ChildDeliveryMode childDeliveryMode) {
        this.childDeliveryMode = childDeliveryMode;
        return this;
    }

    public PregnancyDeliveryRequest childDeliveryOutcome(ChildDeliveryOutcome childDeliveryOutcome) {
        this.childDeliveryOutcome = childDeliveryOutcome;
        return this;
    }

    public PregnancyDeliveryRequest maleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
        return this;
    }

    public PregnancyDeliveryRequest childDeliveryLocation(ChildDeliveryLocation childDeliveryLocation) {
        this.childDeliveryLocation = childDeliveryLocation;
        return this;
    }

    public PregnancyDeliveryRequest childDeliveredBy(ChildDeliveredBy childDeliveredBy) {
        this.childDeliveredBy = childDeliveredBy;
        return this;
    }

    public PregnancyDeliveryRequest deliveryComplications(DeliveryComplications deliveryComplications) {
        this.deliveryComplications = deliveryComplications;
        return this;
    }

    public PregnancyDeliveryRequest vvf(VVF vvf) {
        this.vvf = vvf;
        return this;
    }

    public PregnancyDeliveryRequest maternalDeath(Boolean maternalDeath) {
        this.maternalDeath = maternalDeath;
        return this;
    }

    public PregnancyDeliveryRequest comments(String comments) {
        this.comments = comments;
        return this;
    }

    public PregnancyDeliveryRequest sender(String sender) {
        this.sender = sender;
        return this;
    }
}

