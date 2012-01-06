package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Date;

@TypeDiscriminator("doc.type === 'CWCEnrollment'")
public class CWCEnrollment extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "CWCEnrollment";
    @JsonProperty
    private String patientId;
    @JsonProperty
    private String serialNumber;
    @JsonProperty
    private MotechProgram program;
    @JsonProperty
    private Date registrationDate;
    @JsonProperty
    private String facilityId;

    public CWCEnrollment() {
    }

    public CWCEnrollment(String patientId, MotechProgram program) {
        this.patientId = patientId;
        this.program = program;
    }

    public String getPatientId() {
        return patientId;
    }

    public MotechProgram getProgram() {
        return program;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Date getRegistrationDate() {
        return  registrationDate;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void patienId(String patientId) {
        this.patientId = patientId;
    }

    public void program(MotechProgram program) {
        this.program = program;
    }

    public void serialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void registrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void facilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
