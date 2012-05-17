package org.motechproject.ghana.national.messagegateway.domain;

import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SMSPayloadTest {
    @Test
    public void shouldFillRuntimeValuesOfTheTemplate(){
        assertThat(SMSPayload.fill("hi-${key1}-${key2}", new HashMap<String, String>() {{
            put("${key1}", "value1");
            put("${key2}", "value2");
        }}), is(equalTo("hi-value1-value2")));

        assertThat(SMSPayload.fill("hi", new HashMap<String, String>()), is(equalTo("hi")));
    }
}
