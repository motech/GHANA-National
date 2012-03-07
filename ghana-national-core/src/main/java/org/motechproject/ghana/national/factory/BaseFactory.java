package org.motechproject.ghana.national.factory;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.MobileFormEnum;

public abstract class BaseFactory {

    protected Integer toInteger(String value) {
        return (value != null && !value.equals(Constants.NOT_APPLICABLE)) ? Integer.parseInt(value) : null;
    }

    protected Boolean toBoolean(String value) {
        return (value != null) ? value.equals(Constants.OBSERVATION_YES) : null;
    }

    protected Integer safeEnumValue(MobileFormEnum enumItem) {
        return (enumItem != null) ? Integer.valueOf(enumItem.getNumericValue()) : null;
    }
}
