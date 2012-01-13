package org.motechproject.ghana.national.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Messages {

    @Autowired
    MessageSource messageSource;

    public String message(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}
