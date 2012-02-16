package org.motechproject.ghana.national.service.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PregnancyTerminationRequest {
    private String motechId;
    private String staffId;
    private String facilityId;
    private Date terminationDate;
    private Boolean dead;
    private String terminationType;
    private String terminationProcedure;
    private List<String> complications;
    private Boolean referred;
    private String comments;
    private Boolean postAbortionFPAccepted;
    private Boolean postAbortionFPCounselling;

    public PregnancyTerminationRequest() {
        complications = new ArrayList<String>();
    }

    public String getMotechId() {
        return motechId;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public Boolean isDead() {
        return dead;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getTerminationType() {
        return terminationType;
    }

    public String getTerminationProcedure() {
        return terminationProcedure;
    }

    public List<String> getComplications() {
        return complications;
    }

    public Boolean isReferred() {
        return referred;
    }

    public String getComments() {
        return comments;
    }

    public void setReferred(Boolean referred) {
        this.referred = referred;
    }

    public void addComplication(String complication) {
        complications.add(complication);
    }

    public void setTerminationType(String terminationType) {
        this.terminationType = terminationType;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setTerminationProcedure(String terminationProcedure) {
        this.terminationProcedure = terminationProcedure;
    }

    public void setComplications(List<String> complications) {
        this.complications = complications;
    }

    public Boolean getPostAbortionFPAccepted() {
        return postAbortionFPAccepted;
    }

    public Boolean getPostAbortionFPCounselling() {
        return postAbortionFPCounselling;
    }

    public void setPostAbortionFPAccepted(Boolean postAbortionFPAccepted) {
        this.postAbortionFPAccepted = postAbortionFPAccepted;
    }

    public void setPostAbortionFPCounselling(Boolean postAbortionFPCounselling) {
        this.postAbortionFPCounselling = postAbortionFPCounselling;
    }
}
