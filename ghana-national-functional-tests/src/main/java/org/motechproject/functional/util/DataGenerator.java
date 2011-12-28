package org.motechproject.functional.util;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class DataGenerator {

    public String randomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public String randomPhoneNumber() {
        return "0" + RandomStringUtils.randomNumeric(9);
    }

    public String randomEmailId(){
        return RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".com";
    }
}
