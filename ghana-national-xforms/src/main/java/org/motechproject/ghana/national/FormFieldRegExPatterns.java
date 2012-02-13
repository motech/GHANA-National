package org.motechproject.ghana.national;


public class FormFieldRegExPatterns {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";
    public static final String NAME_PATTERN = "[0-9.\\-\\s]*[a-zA-Z]?[a-zA-Z0-9.\\-\\s]*";
    public static final String MOTECH_ID_PATTERN = "[0-9]{7}";
    public static final String NHIS_NO_PATTERN = "[a-zA-Z0-9.,'/\\\\-\\_\\s]+";
    public static final String PHONE_NO_PATTERN = "0[0-9]{9}";
    public static final String MM_MESSAGE_FORMAT = "([A-Z]+_VOICE|[A-Z]+_TEXT)";
    public static final String MM_MESSAGE_START_WEEK = "([5-9]{1}|[1-8]{1}[0-9]{1}|9[0-2]{1})";
    public static final String GENDER_PATTERN = "[MmFf]";
}
