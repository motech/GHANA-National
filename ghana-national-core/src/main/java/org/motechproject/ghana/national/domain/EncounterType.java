package org.motechproject.ghana.national.domain;

public enum EncounterType {
    TT_VISIT("TTVISIT"),
    ANC_REG_VISIT("ANCREGVISIT"),
    ANC_VISIT("ANCVISIT"),
    CWC_REG_VISIT("CWCREGVISIT"),
    CWC_VISIT("CWCVISIT"),
    PATIENT_HISTORY("PATIENTHISTORY"),
    PREG_REG_VISIT("PREGREGVISIT"),
    PATIENT_REG_VISIT("PATIENTREGVISIT"),
    PATIENT_EDIT_VISIT("PATIENTEDITVISIT"),
    PREG_DEL_NOTIFY_VISIT("PREGDELNOTIFYVISIT"),
    OUTPATIENT_VISIT("OUTPATIENTVISIT"),
    PREG_TERM_VISIT("PREGTERMVISIT"),
    PREG_DEL_VISIT("PREGDELVISIT"),
    BIRTH_VISIT("BIRTHVISIT"),
    PNC_CHILD_VISIT("PNCCHILDVISIT"),
    PNC_MOTHER_VISIT("PNCMOTHERVISIT");

    private String value;

    EncounterType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
