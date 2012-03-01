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

    public BirthOutcome getChildBirthOutcome() {
        return childBirthOutcome;
    }

    public RegistrationType getChildRegistrationType() {
        return childRegistrationType;
    }

    public String getChildMotechId() {
        return childMotechId;
    }

    public String getChildSex() {
        return childSex;
    }

    public String getChildFirstName() {
        return childFirstName;
    }

    public String getChildWeight() {
        return childWeight;
    }
}
