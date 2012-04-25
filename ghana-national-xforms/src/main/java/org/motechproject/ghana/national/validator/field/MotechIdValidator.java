package org.motechproject.ghana.national.validator.field;

import org.motechproject.MotechException;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FieldValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;
import org.openmrs.patient.UnallowedIdentifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotechIdValidator implements FieldValidator<MotechId> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public FormError validate(Object fieldValue, String fieldName, Class fieldType, MotechId annotation) {
        if (fieldValue != null) {
            VerhoeffValidator validator = getValidatorInstance(annotation.validator());
            if (fieldType == String.class) {
                try{
                    if (!validator.isValid(String.valueOf(fieldValue))) {
                        return new FormError(fieldName, "is invalid");
                    }
                }catch (UnallowedIdentifierException e){
                    return new FormError(fieldName, "is invalid");
                }

            }
        }
        return null;
    }

    VerhoeffValidator getValidatorInstance(Class<? extends VerhoeffValidator> validator) {
        try {
            return validator.newInstance();
        } catch (Exception e) {
            log.error("Instantiation of Validator failed: ", e);
            throw new MotechException("Instantiation of Validator failed: ", e);
        }
    }
}
