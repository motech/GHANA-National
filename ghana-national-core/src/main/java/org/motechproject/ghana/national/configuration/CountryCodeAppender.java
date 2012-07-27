package org.motechproject.ghana.national.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CountryCodeAppender {
    @Value("#{ghanaNationalProperties['country.code']}")
    private String countryCode;


    public String apply(String phoneNumber) {
        if (isTenDigitAndStartsWithZero(phoneNumber))
            return replaceFirstZeroAndPrefixCountryCode(phoneNumber);
        return phoneNumber;
    }

    private String replaceFirstZeroAndPrefixCountryCode(String phoneNumber) {
        return countryCode + phoneNumber.substring(1, phoneNumber.length());
    }

    private boolean isTenDigitAndStartsWithZero(String phoneNumber) {
        return  (phoneNumber.length() == 10 && phoneNumber.startsWith("0"));
    }
}