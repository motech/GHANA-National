package org.motechproject.ghana.national.validator.patient;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.MOTECH_ID_ATTRIBUTE_NAME;
import static org.motechproject.ghana.national.domain.EncounterType.CWC_REG_VISIT;

public class EnrolledToCWC extends PatientValidator {

    private AllEncounters allEncounters;

    public EnrolledToCWC(AllEncounters allEncounters) {
        this.allEncounters = allEncounters;
    }

    @Override
    public List<FormError> validate(final Patient patient, List<FormBean> formsSubmitted) {
        return allEncounters.getLatest(patient.getMotechId(), CWC_REG_VISIT.value()) == null ?
                Arrays.asList(new FormError(MOTECH_ID_ATTRIBUTE_NAME, "not registered for CWC")) : new ArrayList<FormError>();
    }
}
