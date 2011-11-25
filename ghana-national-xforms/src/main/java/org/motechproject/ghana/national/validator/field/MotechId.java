package org.motechproject.ghana.national.validator.field;

import org.motechproject.mobileforms.api.validator.annotations.ValidationMarker;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ValidationMarker(handler = MotechIdValidator.class)
public @interface MotechId {
    Class<? extends VerhoeffValidator> validator();
}

