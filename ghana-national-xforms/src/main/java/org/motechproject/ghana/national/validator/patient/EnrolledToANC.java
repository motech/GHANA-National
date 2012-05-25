package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;

public class EnrolledToANC extends PatientValidator {

    private AllEncounters allEncounters;

    public EnrolledToANC(AllEncounters allEncounters) {
        this.allEncounters = allEncounters;
    }

    @Override
    public List<FormError> validate(final Patient patient, List<FormBean> formsSubmitted) {
        return allEncounters.getLatest(patient.getMotechId(), ANC_REG_VISIT.value()) == null ?
                Arrays.asList(new FormError(patient.getMotechId(), "not registered for ANC")) : new ArrayList<FormError>();
    }
}
