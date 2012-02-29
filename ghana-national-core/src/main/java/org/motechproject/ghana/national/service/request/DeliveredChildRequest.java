package org.motechproject.ghana.national.service.request;

import org.motechproject.ghana.national.domain.BirthOutcome;
import org.motechproject.ghana.national.domain.RegistrationType;

public class DeliveredChildRequest {
    private BirthOutcome childBirthOutcome;
    private RegistrationType childRegistrationType;
    private String childMotechId;
    private String childSex;
    private String childFirstName;
    private String childWeight;

    public DeliveredChildRequest childBirthOutcome(BirthOutcome childBirthOutcome) {
        this.childBirthOutcome = childBirthOutcome;
        return this;
    }

    public DeliveredChildRequest childRegistrationType(RegistrationType childRegistrationType) {
        this.childRegistrationType = childRegistrationType;
        return this;
    }

    public DeliveredChildRequest childMotechId(String childMotechId) {
        this.childMotechId = childMotechId;
        return this;
    }

    public DeliveredChildRequest childSex(String childSex) {
        this.childSex = childSex;
        return this;
    }

    public DeliveredChildRequest childFirstName(String childFirstName) {
        this.childFirstName = childFirstName;
        return this;
    }

    public DeliveredChildRequest childWeight(String childWeight) {
        this.childWeight = childWeight;
        return this;
    }

    public BirthOutcome getchildBirthOutcome() {
        return childBirthOutcome;
    }

    public RegistrationType getchildRegistrationType() {
        return childRegistrationType;
    }

    public String getchildMotechId() {
        return childMotechId;
    }

    public String getchildSex() {
        return childSex;
    }

    public String getchildFirstName() {
        return childFirstName;
    }

    public String getchildWeight() {
        return childWeight;
    }
}
