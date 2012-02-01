package org.motechproject.ghana.national.functional.util;

import org.springframework.stereotype.Component;

@Component
public class PlatformSpecificExecutor {
    public PlatformSpecificExecutor() {
    }

    public void execute(Code code) {
        if (System.getProperty("os.name").contains("Wind")) {
            code.windows();
        } else {
            code.linux();
        }
    }

    public static interface Code {
        void windows();
        void linux();
    }

}
