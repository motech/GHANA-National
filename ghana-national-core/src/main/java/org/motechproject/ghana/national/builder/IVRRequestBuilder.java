package org.motechproject.ghana.national.builder;

import java.util.HashMap;

public class IVRRequestBuilder {

    public static final String CALLBACK_URL = "callback_url";

    public static HashMap<String, String> build(final String url) {
        return new HashMap<String, String>(){{
            put(CALLBACK_URL, url);
        }};
    }
}
