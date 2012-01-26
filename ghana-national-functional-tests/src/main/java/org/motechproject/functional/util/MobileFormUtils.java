package org.motechproject.functional.util;

import java.util.Map;

public class MobileFormUtils {

    public static void updateFieldName(Map<String, String> fields, String fieldName, String newFieldName){
        fields.put(newFieldName, fields.get(fieldName));
        fields.remove(fieldName);
    }

    public static void updateFieldByNameAndValue(Map<String, String> fields, String fieldName, String newFieldName, String value){
        fields.put(newFieldName, value);
        fields.remove(fieldName);
    }
}
