package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Configuration'")
public class Configuration extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "Configuration";

    @JsonProperty
    private String propertyName;

    @JsonProperty
    private String value;

    public Configuration() {
    }

    public Configuration(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public Configuration(String type, String propertyName, String value) {
        super(type);
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value= value;
    }
}

