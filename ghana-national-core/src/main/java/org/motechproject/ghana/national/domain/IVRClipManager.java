package org.motechproject.ghana.national.domain;

import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("ivrClipManager")
public class IVRClipManager {

    public static final String URL_SEPARATOR = "/";
    public static final String AUDIO_FILE_EXTN = ".wav";

    @Value("#{verboiceProperties['resource.url']}")
    private String resourceBaseUrl;

    public String urlFor(String messageName, Language language) {
        return resourceBaseUrl + language.name() + URL_SEPARATOR + messageName + AUDIO_FILE_EXTN;
    }

    public String urlForError(Language language) {
        return resourceBaseUrl + language.name() + URL_SEPARATOR + AudioPrompts.UNEXPECTED_ERROR + AUDIO_FILE_EXTN;
    }
}
