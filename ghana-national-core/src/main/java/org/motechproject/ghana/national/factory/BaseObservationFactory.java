package org.motechproject.ghana.national.factory;

import org.motechproject.mrs.model.MRSObservation;

import java.util.Date;
import java.util.HashSet;

public abstract class BaseObservationFactory extends BaseFactory {
    public <T> void setObservation(HashSet<MRSObservation> mrsObservations, Date registrationDate, String conceptType, T value) {
        if (value != null)
            mrsObservations.add(new MRSObservation<T>(registrationDate, conceptType, value));
    }
}
