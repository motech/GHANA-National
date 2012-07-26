package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'IVRChannelMapping'")
public class IVRChannelMapping extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "IVRChannelMapping";
    @JsonProperty
    private String phoneNumberPattern;
    @JsonProperty
    private String ivrChannel;

    public IVRChannelMapping() {
    }

    public String getPhoneNumberPattern() {
        return phoneNumberPattern;
    }

    public String getIvrChannel() {
        return ivrChannel;
    }

    public IVRChannelMapping phoneNumberPattern(String phoneNumberPattern){
        this.phoneNumberPattern = phoneNumberPattern;
        return this;
    }

    public IVRChannelMapping ivrChannel(String ivrChannel){
        this.ivrChannel = ivrChannel;
        return this;
    }
}
