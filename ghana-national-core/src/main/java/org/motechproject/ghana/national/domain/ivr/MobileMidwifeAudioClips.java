package org.motechproject.ghana.national.domain.ivr;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public enum MobileMidwifeAudioClips {
    CHILD_CARE_WEEK_1(asList("b_wk1a", "b_wk1b", "b_wk1c"), asList("b_wK1a_prompt", "b_wK1b_prompt", "b_wK1c_prompt")),
    CHILD_CARE_WEEK_2(asList("b_wk2a", "b_wk2b", "b_wk2c"), asList("b_wK2a_prompt", "b_wK2b_prompt", "b_wK2c_prompt")),
    CHILD_CARE_WEEK_3(asList("b_wk3a", "b_wk3b", "b_wk3c"), asList("b_wK3a_prompt", "b_wK3b_prompt", "b_wK3c_prompt")),
    CHILD_CARE_WEEK_4(asList("b_wk4a", "b_wk4b", "b_wk4c"), asList("b_wK4a_prompt", "b_wK4b_prompt", "b_wK4c_prompt")),
    CHILD_CARE_WEEK_5(asList("b_wk5"), asList("b_wK5_prompt")),
    CHILD_CARE_WEEK_6(asList("b_wk6"), asList("b_wK6_prompt")),
    CHILD_CARE_WEEK_7(asList("b_wk7"), asList("b_wK7_prompt")),
    CHILD_CARE_WEEK_8(asList("b_wk8"), asList("b_wK8_prompt")),
    CHILD_CARE_WEEK_9(asList("b_wk9"), asList("b_wK9_prompt")),
    CHILD_CARE_WEEK_10(asList("b_wk10"), asList("b_wK10_prompt")),
    CHILD_CARE_WEEK_11(asList("b_wk11"), asList("b_wK11_prompt")),
    CHILD_CARE_WEEK_12(asList("b_wk12"), asList("b_wK12_prompt")),
    CHILD_CARE_WEEK_13(asList("b_wk13"), asList("b_wK13_prompt")),
    CHILD_CARE_WEEK_14(asList("b_wk14"), asList("b_wK14_prompt")),
    CHILD_CARE_WEEK_15(asList("b_wk15"), asList("b_wK15_prompt")),
    CHILD_CARE_WEEK_16(asList("b_wk16"), asList("b_wK16_prompt")),
    CHILD_CARE_WEEK_17(asList("b_wk17"), asList("b_wK17_prompt")),
    CHILD_CARE_WEEK_18(asList("b_wk18"), asList("b_wK18_prompt")),
    CHILD_CARE_WEEK_19(asList("b_wk19"), asList("b_wK19_prompt")),
    CHILD_CARE_WEEK_20(asList("b_wk20"), asList("b_wK20_prompt")),
    CHILD_CARE_WEEK_21(asList("b_wk21"), asList("b_wK21_prompt")),
    CHILD_CARE_WEEK_22(asList("b_wk22"), asList("b_wK22_prompt")),
    CHILD_CARE_WEEK_23(asList("b_wk23"), asList("b_wK23_prompt")),
    CHILD_CARE_WEEK_24(asList("b_wk24"), asList("b_wK24_prompt")),
    CHILD_CARE_WEEK_25(asList("b_wk25"), asList("b_wK25_prompt")),
    CHILD_CARE_WEEK_26(asList("b_wk26"), asList("b_wK26_prompt")),
    CHILD_CARE_WEEK_27(asList("b_wk27"), asList("b_wK27_prompt")),
    CHILD_CARE_WEEK_28(asList("b_wk28"), asList("b_wK28_prompt")),
    CHILD_CARE_WEEK_29(asList("b_wk29"), asList("b_wK29_prompt")),
    CHILD_CARE_WEEK_30(asList("b_wk30"), asList("b_wK30_prompt")),
    CHILD_CARE_WEEK_31(asList("b_wk31"), asList("b_wK31_prompt")),
    CHILD_CARE_WEEK_32(asList("b_wk32"), asList("b_wK32_prompt")),
    CHILD_CARE_WEEK_33(asList("b_wk33"), asList("b_wK33_prompt")),
    CHILD_CARE_WEEK_34(asList("b_wk34"), asList("b_wK34_prompt")),
    CHILD_CARE_WEEK_35(asList("b_wk35"), asList("b_wK35_prompt")),
    CHILD_CARE_WEEK_36(asList("b_wk36"), asList("b_wK36_prompt")),
    CHILD_CARE_WEEK_37(asList("b_wk37"), asList("b_wK37_prompt")),
    CHILD_CARE_WEEK_38(asList("b_wk38"), asList("b_wK38_prompt")),
    CHILD_CARE_WEEK_39(asList("b_wk39"), asList("b_wK39_prompt")),
    CHILD_CARE_WEEK_40(asList("b_wk40"), asList("b_wK40_prompt")),
    CHILD_CARE_WEEK_41(asList("b_wk41"), asList("b_wK41_prompt")),
    CHILD_CARE_WEEK_42(asList("b_wk42"), asList("b_wK42_prompt")),
    CHILD_CARE_WEEK_43(asList("b_wk43"), asList("b_wK43_prompt")),
    CHILD_CARE_WEEK_44(asList("b_wk44"), asList("b_wK44_prompt")),
    CHILD_CARE_WEEK_45(asList("b_wk45"), asList("b_wK45_prompt")),
    CHILD_CARE_WEEK_46(asList("b_wk46"), asList("b_wK46_prompt")),
    CHILD_CARE_WEEK_47(asList("b_wk47"), asList("b_wK47_prompt")),
    CHILD_CARE_WEEK_48(asList("b_wk48"), asList("b_wK48_prompt")),
    CHILD_CARE_WEEK_49(asList("b_wk49"), asList("b_wK49_prompt")),
    CHILD_CARE_WEEK_50(asList("b_wk50"), asList("b_wK50_prompt")),
    CHILD_CARE_WEEK_51(asList("b_wk51"), asList("b_wK51_prompt")),
    CHILD_CARE_WEEK_52(asList("b_wk52"), asList("b_wK52_prompt")),
    PREGNANCY_WEEK_5(asList("p_wk5a", "p_wk5b", "p_wk5c"), asList("p_wK5a_prompt", "p_wK5b_prompt", "p_wK5c_prompt")),
    PREGNANCY_WEEK_6(asList("p_wk6a", "p_wk6b", "p_wk6c"), asList("p_wK6a_prompt", "p_wK6b_prompt", "p_wK6c_prompt")),
    PREGNANCY_WEEK_7(asList("p_wk7a", "p_wk7b", "p_wk7c"), asList("p_wK7a_prompt", "p_wK7b_prompt", "p_wK7c_prompt")),
    PREGNANCY_WEEK_8(asList("p_wk8a", "p_wk8b", "p_wk8c"), asList("p_wK8a_prompt", "p_wK8b_prompt", "p_wK8c_prompt")),
    PREGNANCY_WEEK_9(asList("p_wk9a", "p_wk9b", "p_wk9c"), asList("p_wK9a_prompt", "p_wK9b_prompt", "p_wK9c_prompt")),
    PREGNANCY_WEEK_10(asList("p_wk10a", "p_wk10b", "p_wk10c"), asList("p_wK10a_prompt", "p_wK10b_prompt", "p_wK10c_prompt")),
    PREGNANCY_WEEK_11(asList("p_wk11a", "p_wk11b", "p_wk11c"), asList("p_wK11a_prompt", "p_wK11b_prompt", "p_wK11c_prompt")),
    PREGNANCY_WEEK_12(asList("p_wk12a", "p_wk12b", "p_wk12c"), asList("p_wK12a_prompt", "p_wK12b_prompt", "p_wK12c_prompt")),
    PREGNANCY_WEEK_13(asList("p_wk13a", "p_wk13b", "p_wk13c"), asList("p_wK13a_prompt", "p_wK13b_prompt", "p_wK13c_prompt")),
    PREGNANCY_WEEK_14(asList("p_wk14a", "p_wk14b", "p_wk14c"), asList("p_wK14a_prompt", "p_wK14b_prompt", "p_wK14c_prompt")),
    PREGNANCY_WEEK_15(asList("p_wk15a", "p_wk15b", "p_wk15c"), asList("p_wK15a_prompt", "p_wK15b_prompt", "p_wK15c_prompt")),
    PREGNANCY_WEEK_16(asList("p_wk16a", "p_wk16b", "p_wk16c"), asList("p_wK16a_prompt", "p_wK16b_prompt", "p_wK16c_prompt")),
    PREGNANCY_WEEK_17(asList("p_wk17a", "p_wk17b", "p_wk17c"), asList("p_wK17a_prompt", "p_wK17b_prompt", "p_wK17c_prompt")),
    PREGNANCY_WEEK_18(asList("p_wk18a", "p_wk18b", "p_wk18c"), asList("p_wK18a_prompt", "p_wK18b_prompt", "p_wK18c_prompt")),
    PREGNANCY_WEEK_19(asList("p_wk19a", "p_wk19b", "p_wk19c"), asList("p_wK19a_prompt", "p_wK19b_prompt", "p_wK19c_prompt")),
    PREGNANCY_WEEK_20(asList("p_wk20a", "p_wk20b", "p_wk20c"), asList("p_wK20a_prompt", "p_wK20b_prompt", "p_wK20c_prompt")),
    PREGNANCY_WEEK_21(asList("p_wk21a", "p_wk21b", "p_wk21c"), asList("p_wK21a_prompt", "p_wK21b_prompt", "p_wK21c_prompt")),
    PREGNANCY_WEEK_22(asList("p_wk22a", "p_wk22b", "p_wk22c"), asList("p_wK22a_prompt", "p_wK22b_prompt", "p_wK22c_prompt")),
    PREGNANCY_WEEK_23(asList("p_wk23a", "p_wk23b", "p_wk23c"), asList("p_wK23a_prompt", "p_wK23b_prompt", "p_wK23c_prompt")),
    PREGNANCY_WEEK_24(asList("p_wk24a", "p_wk24b", "p_wk24c"), asList("p_wK24a_prompt", "p_wK24b_prompt", "p_wK24c_prompt")),
    PREGNANCY_WEEK_25(asList("p_wk25a", "p_wk25b", "p_wk25c"), asList("p_wK25a_prompt", "p_wK25b_prompt", "p_wK25c_prompt")),
    PREGNANCY_WEEK_26(asList("p_wk26a", "p_wk26b", "p_wk26c"), asList("p_wK26a_prompt", "p_wK26b_prompt", "p_wK26c_prompt")),
    PREGNANCY_WEEK_27(asList("p_wk27a", "p_wk27b", "p_wk27c"), asList("p_wK27a_prompt", "p_wK27b_prompt", "p_wK27c_prompt")),
    PREGNANCY_WEEK_28(asList("p_wk28a", "p_wk28b", "p_wk28c"), asList("p_wK28a_prompt", "p_wK28b_prompt", "p_wK28c_prompt")),
    PREGNANCY_WEEK_29(asList("p_wk29a", "p_wk29b", "p_wk29c"), asList("p_wK29a_prompt", "p_wK29b_prompt", "p_wK29c_prompt")),
    PREGNANCY_WEEK_30(asList("p_wk30a", "p_wk30b", "p_wk30c"), asList("p_wK30a_prompt", "p_wK30b_prompt", "p_wK30c_prompt")),
    PREGNANCY_WEEK_31(asList("p_wk31a", "p_wk31b", "p_wk31c"), asList("p_wK31a_prompt", "p_wK31b_prompt", "p_wK31c_prompt")),
    PREGNANCY_WEEK_32(asList("p_wk32a", "p_wk32b", "p_wk32c"), asList("p_wK32a_prompt", "p_wK32b_prompt", "p_wK32c_prompt")),
    PREGNANCY_WEEK_33(asList("p_wk33a", "p_wk33b", "p_wk33c"), asList("p_wK33a_prompt", "p_wK33b_prompt", "p_wK33c_prompt")),
    PREGNANCY_WEEK_34(asList("p_wk34a", "p_wk34b", "p_wk34c"), asList("p_wK34a_prompt", "p_wK34b_prompt", "p_wK34c_prompt")),
    PREGNANCY_WEEK_35(asList("p_wk35a", "p_wk35b", "p_wk35c"), asList("p_wK35a_prompt", "p_wK35b_prompt", "p_wK35c_prompt")),
    PREGNANCY_WEEK_36(asList("p_wk36a", "p_wk36b", "p_wk36c"), asList("p_wK36a_prompt", "p_wK36b_prompt", "p_wK36c_prompt")),
    PREGNANCY_WEEK_37(asList("p_wk37a", "p_wk37b", "p_wk37c"), asList("p_wK37a_prompt", "p_wK37b_prompt", "p_wK37c_prompt")),
    PREGNANCY_WEEK_38(asList("p_wk38a", "p_wk38b", "p_wk38c"), asList("p_wK38a_prompt", "p_wK38b_prompt", "p_wK38c_prompt")),
    PREGNANCY_WEEK_39(asList("p_wk39a", "p_wk39b", "p_wk39c"), asList("p_wK39a_prompt", "p_wK39b_prompt", "p_wK39c_prompt")),
    PREGNANCY_WEEK_40(asList("p_wk40a", "p_wk40b", "p_wk40c"), asList("p_wK40a_prompt", "p_wK40b_prompt", "p_wK40c_prompt"));


    private final List<String> clipNames;
    private final List<String> promptClipNames;

    MobileMidwifeAudioClips(List<String> clipNames, List<String> promptClipNames) {
        this.clipNames = clipNames;
        this.promptClipNames = promptClipNames;
    }

    public List<String> getClipNames() {
        return clipNames;
    }

    public List<String> getPromptClipNames() {
        return promptClipNames;
    }

    public static MobileMidwifeAudioClips instance(String programName, String week){
        return MobileMidwifeAudioClips.valueOf(programName.toUpperCase() + "_WEEK_" + week);
    }
}


