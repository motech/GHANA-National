package org.motechproject.ghana.national.bean;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.*;

public class DeliveryForm extends FormBean {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";

    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String staffId;

    @RegEx(pattern = MOTECH_ID_PATTERN)
    private String motechId;

    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;

    private DateTime date;
    private ChildDeliveryMode mode;
    private ChildDeliveryOutcome outcome;
    private Boolean maleInvolved;
    private ChildDeliveryLocation deliveryLocation;
    private ChildDeliveredBy deliveredBy;
    private DeliveryComplications complications;
    private VVF vvf;
    private Boolean maternalDeath;
    private String comments;
    private BirthOutcome child1Outcome;
    private RegistrationType child1RegistrationType;

    @RegEx(pattern = MOTECH_ID_PATTERN)
    private String child1MotechId;

    @RegEx(pattern = GENDER_PATTERN)
    private String child1Sex;
    private String child1FirstName;
    private String child1Weight;
    private BirthOutcome child2Outcome;
    private RegistrationType child2RegistrationType;

    @RegEx(pattern = MOTECH_ID_PATTERN)
    private String child2MotechId;

    @RegEx(pattern = GENDER_PATTERN)
    private String child2Sex;
    private String child2FirstName;
    private String child2Weight;
    private BirthOutcome child3Outcome;
    private RegistrationType child3RegistrationType;

    @RegEx(pattern = MOTECH_ID_PATTERN)
    private String child3MotechId;

    @RegEx(pattern = GENDER_PATTERN)
    private String child3Sex;
    private String child3FirstName;
    private String child3Weight;

    @RegEx(pattern = PHONE_NO_PATTERN)
    private String sender;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public ChildDeliveryMode getMode() {
        return mode;
    }

    public void setMode(ChildDeliveryMode mode) {
        this.mode = mode;
    }

    public ChildDeliveryOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(ChildDeliveryOutcome outcome) {
        this.outcome = outcome;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public void setMaleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
    }

    public ChildDeliveryLocation getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(ChildDeliveryLocation deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public ChildDeliveredBy getDeliveredBy() {
        return deliveredBy;
    }

    public void setDeliveredBy(ChildDeliveredBy deliveredBy) {
        this.deliveredBy = deliveredBy;
    }

    public DeliveryComplications getComplications() {
        return complications;
    }

    public void setComplications(DeliveryComplications complications) {
        this.complications = complications;
    }

    public VVF getVvf() {
        return vvf;
    }

    public void setVvf(VVF vvf) {
        this.vvf = vvf;
    }

    public Boolean getMaternalDeath() {
        return maternalDeath;
    }

    public void setMaternalDeath(Boolean maternalDeath) {
        this.maternalDeath = maternalDeath;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public BirthOutcome getChild1Outcome() {
        return child1Outcome;
    }

    public void setChild1Outcome(BirthOutcome child1Outcome) {
        this.child1Outcome = child1Outcome;
    }

    public RegistrationType getChild1RegistrationType() {
        return child1RegistrationType;
    }

    public void setChild1RegistrationType(RegistrationType child1RegistrationType) {
        this.child1RegistrationType = child1RegistrationType;
    }

    public String getChild1MotechId() {
        return child1MotechId;
    }

    public void setChild1MotechId(String child1MotechId) {
        this.child1MotechId = child1MotechId;
    }

    public String getChild1Sex() {
        return child1Sex;
    }

    public void setChild1Sex(String child1Sex) {
        this.child1Sex = child1Sex;
    }

    public String getChild1FirstName() {
        return child1FirstName;
    }

    public void setChild1FirstName(String child1FirstName) {
        this.child1FirstName = child1FirstName;
    }

    public String getChild1Weight() {
        return child1Weight;
    }

    public void setChild1Weight(String child1Weight) {
        this.child1Weight = child1Weight;
    }

    public BirthOutcome getChild2Outcome() {
        return child2Outcome;
    }

    public void setChild2Outcome(BirthOutcome child2Outcome) {
        this.child2Outcome = child2Outcome;
    }

    public RegistrationType getChild2RegistrationType() {
        return child2RegistrationType;
    }

    public void setChild2RegistrationType(RegistrationType child2RegistrationType) {
        this.child2RegistrationType = child2RegistrationType;
    }

    public String getChild2MotechId() {
        return child2MotechId;
    }

    public void setChild2MotechId(String child2MotechId) {
        this.child2MotechId = child2MotechId;
    }

    public String getChild2Sex() {
        return child2Sex;
    }

    public void setChild2Sex(String child2Sex) {
        this.child2Sex = child2Sex;
    }

    public String getChild2FirstName() {
        return child2FirstName;
    }

    public void setChild2FirstName(String child2FirstName) {
        this.child2FirstName = child2FirstName;
    }

    public String getChild2Weight() {
        return child2Weight;
    }

    public void setChild2Weight(String child2Weight) {
        this.child2Weight = child2Weight;
    }

    public BirthOutcome getChild3Outcome() {
        return child3Outcome;
    }

    public void setChild3Outcome(BirthOutcome child3Outcome) {
        this.child3Outcome = child3Outcome;
    }

    public RegistrationType getChild3RegistrationType() {
        return child3RegistrationType;
    }

    public void setChild3RegistrationType(RegistrationType child3RegistrationType) {
        this.child3RegistrationType = child3RegistrationType;
    }

    public String getChild3MotechId() {
        return child3MotechId;
    }

    public void setChild3MotechId(String child3MotechId) {
        this.child3MotechId = child3MotechId;
    }

    public String getChild3Sex() {
        return child3Sex;
    }

    public void setChild3Sex(String child3Sex) {
        this.child3Sex = child3Sex;
    }

    public String getChild3FirstName() {
        return child3FirstName;
    }

    public void setChild3FirstName(String child3FirstName) {
        this.child3FirstName = child3FirstName;
    }

    public String getChild3Weight() {
        return child3Weight;
    }

    public void setChild3Weight(String child3Weight) {
        this.child3Weight = child3Weight;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
