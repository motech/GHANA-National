package org.motechproject.ghana.national.domain.ivr;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public enum MobileMidwifeAudioClips {
    CHILDCARE_WEEK_1(asList("b_wk1a", "b_wk1b", "b_wk1c"), asList("b_wK1a_prompt", "b_wK1b_prompt", "b_wK1c_prompt")),
    CHILDCARE_WEEK_2(asList("b_wk2a", "b_wk2b", "b_wk2c"), asList("b_wK2a_prompt", "b_wK2b_prompt", "b_wK2c_prompt")),
    CHILDCARE_WEEK_3(asList("b_wk3a", "b_wk3b", "b_wk3c"), asList("b_wK3a_prompt", "b_wK3b_prompt", "b_wK3c_prompt")),
    CHILDCARE_WEEK_4(asList("b_wk4a", "b_wk4b", "b_wk4c"), asList("b_wK4a_prompt", "b_wK4b_prompt", "b_wK4c_prompt")),
    CHILDCARE_WEEK_5(asList("b_wk5"), asList("b_wK5_prompt")),
    CHILDCARE_WEEK_6(asList("b_wk6"), asList("b_wK6_prompt")),
    CHILDCARE_WEEK_7(asList("b_wk7"), asList("b_wK7_prompt")),
    CHILDCARE_WEEK_8(asList("b_wk8"), asList("b_wK8_prompt")),
    CHILDCARE_WEEK_9(asList("b_wk9"), asList("b_wK9_prompt")),
    CHILDCARE_WEEK_10(asList("b_wk10"), asList("b_wK10_prompt")),
    CHILDCARE_WEEK_11(asList("b_wk11"), asList("b_wK11_prompt")),
    CHILDCARE_WEEK_12(asList("b_wk12"), asList("b_wK12_prompt")),
    CHILDCARE_WEEK_13(asList("b_wk13"), asList("b_wK13_prompt")),
    CHILDCARE_WEEK_14(asList("b_wk14"), asList("b_wK14_prompt")),
    CHILDCARE_WEEK_15(asList("b_wk15"), asList("b_wK15_prompt")),
    CHILDCARE_WEEK_16(asList("b_wk16"), asList("b_wK16_prompt")),
    CHILDCARE_WEEK_17(asList("b_wk17"), asList("b_wK17_prompt")),
    CHILDCARE_WEEK_18(asList("b_wk18"), asList("b_wK18_prompt")),
    CHILDCARE_WEEK_19(asList("b_wk19"), asList("b_wK19_prompt")),
    CHILDCARE_WEEK_20(asList("b_wk20"), asList("b_wK20_prompt")),
    CHILDCARE_WEEK_21(asList("b_wk21"), asList("b_wK21_prompt")),
    CHILDCARE_WEEK_22(asList("b_wk22"), asList("b_wK22_prompt")),
    CHILDCARE_WEEK_23(asList("b_wk23"), asList("b_wK23_prompt")),
    CHILDCARE_WEEK_24(asList("b_wk24"), asList("b_wK24_prompt")),
    CHILDCARE_WEEK_25(asList("b_wk25"), asList("b_wK25_prompt")),
    CHILDCARE_WEEK_26(asList("b_wk26"), asList("b_wK26_prompt")),
    CHILDCARE_WEEK_27(asList("b_wk27"), asList("b_wK27_prompt")),
    CHILDCARE_WEEK_28(asList("b_wk28"), asList("b_wK28_prompt")),
    CHILDCARE_WEEK_29(asList("b_wk29"), asList("b_wK29_prompt")),
    CHILDCARE_WEEK_30(asList("b_wk30"), asList("b_wK30_prompt")),
    CHILDCARE_WEEK_31(asList("b_wk31"), asList("b_wK31_prompt")),
    CHILDCARE_WEEK_32(asList("b_wk32"), asList("b_wK32_prompt")),
    CHILDCARE_WEEK_33(asList("b_wk33"), asList("b_wK33_prompt")),
    CHILDCARE_WEEK_34(asList("b_wk34"), asList("b_wK34_prompt")),
    CHILDCARE_WEEK_35(asList("b_wk35"), asList("b_wK35_prompt")),
    CHILDCARE_WEEK_36(asList("b_wk36"), asList("b_wK36_prompt")),
    CHILDCARE_WEEK_37(asList("b_wk37"), asList("b_wK37_prompt")),
    CHILDCARE_WEEK_38(asList("b_wk38"), asList("b_wK38_prompt")),
    CHILDCARE_WEEK_39(asList("b_wk39"), asList("b_wK39_prompt")),
    CHILDCARE_WEEK_40(asList("b_wk40"), asList("b_wK40_prompt")),
    CHILDCARE_WEEK_41(asList("b_wk41"), asList("b_wK41_prompt")),
    CHILDCARE_WEEK_42(asList("b_wk42"), asList("b_wK42_prompt")),
    CHILDCARE_WEEK_43(asList("b_wk43"), asList("b_wK43_prompt")),
    CHILDCARE_WEEK_44(asList("b_wk44"), asList("b_wK44_prompt")),
    CHILDCARE_WEEK_45(asList("b_wk45"), asList("b_wK45_prompt")),
    CHILDCARE_WEEK_46(asList("b_wk46"), asList("b_wK46_prompt")),
    CHILDCARE_WEEK_47(asList("b_wk47"), asList("b_wK47_prompt")),
    CHILDCARE_WEEK_48(asList("b_wk48"), asList("b_wK48_prompt")),
    CHILDCARE_WEEK_49(asList("b_wk49"), asList("b_wK49_prompt")),
    CHILDCARE_WEEK_50(asList("b_wk50"), asList("b_wK50_prompt")),
    CHILDCARE_WEEK_51(asList("b_wk51"), asList("b_wK51_prompt")),
    CHILDCARE_WEEK_52(asList("b_wk52"), asList("b_wK52_prompt")),
    PREGNANCY_WEEK_5(asList("p_wK5a", "p_wK5b", "p_wK5c"), asList("p_wK5a_prompt", "p_wK5b_prompt", "p_wK5c_prompt")),
    PREGNANCY_WEEK_6(asList("p_wK6a", "p_wK6b", "p_wK6c"), asList("p_wK6a_prompt", "p_wK6b_prompt", "p_wK6c_prompt")),
    PREGNANCY_WEEK_7(asList("p_wK7a", "p_wK7b", "p_wK7c"), asList("p_wK7a_prompt", "p_wK7b_prompt", "p_wK7c_prompt")),
    PREGNANCY_WEEK_8(asList("p_wK8a", "p_wK8b", "p_wK8c"), asList("p_wK8a_prompt", "p_wK8b_prompt", "p_wK8c_prompt")),
    PREGNANCY_WEEK_9(asList("p_wK9a", "p_wK9b", "p_wK9c"), asList("p_wK9a_prompt", "p_wK9b_prompt", "p_wK9c_prompt")),
    PREGNANCY_WEEK_10(asList("p_wK10a", "p_wK10b", "p_wK10c"), asList("p_wK10a_prompt", "p_wK10b_prompt", "p_wK10c_prompt")),
    PREGNANCY_WEEK_11(asList("p_wK11a", "p_wK11b", "p_wK11c"), asList("p_wK11a_prompt", "p_wK11b_prompt", "p_wK11c_prompt")),
    PREGNANCY_WEEK_12(asList("p_wK12a", "p_wK12b", "p_wK12c"), asList("p_wK12a_prompt", "p_wK12b_prompt", "p_wK12c_prompt")),
    PREGNANCY_WEEK_13(asList("p_wK13a", "p_wK13b", "p_wK13c"), asList("p_wK13a_prompt", "p_wK13b_prompt", "p_wK13c_prompt")),
    PREGNANCY_WEEK_14(asList("p_wK14a", "p_wK14b", "p_wK14c"), asList("p_wK14a_prompt", "p_wK14b_prompt", "p_wK14c_prompt")),
    PREGNANCY_WEEK_15(asList("p_wK15a", "p_wK15b", "p_wK15c"), asList("p_wK15a_prompt", "p_wK15b_prompt", "p_wK15c_prompt")),
    PREGNANCY_WEEK_16(asList("p_wK16a", "p_wK16b", "p_wK16c"), asList("p_wK16a_prompt", "p_wK16b_prompt", "p_wK16c_prompt")),
    PREGNANCY_WEEK_17(asList("p_wK17a", "p_wK17b", "p_wK17c"), asList("p_wK17a_prompt", "p_wK17b_prompt", "p_wK17c_prompt")),
    PREGNANCY_WEEK_18(asList("p_wK18a", "p_wK18b", "p_wK18c"), asList("p_wK18a_prompt", "p_wK18b_prompt", "p_wK18c_prompt")),
    PREGNANCY_WEEK_19(asList("p_wK19a", "p_wK19b", "p_wK19c"), asList("p_wK19a_prompt", "p_wK19b_prompt", "p_wK19c_prompt")),
    PREGNANCY_WEEK_20(asList("p_wK20a", "p_wK20b", "p_wK20c"), asList("p_wK20a_prompt", "p_wK20b_prompt", "p_wK20c_prompt")),
    PREGNANCY_WEEK_21(asList("p_wK21a", "p_wK21b", "p_wK21c"), asList("p_wK21a_prompt", "p_wK21b_prompt", "p_wK21c_prompt")),
    PREGNANCY_WEEK_22(asList("p_wK22a", "p_wK22b", "p_wK22c"), asList("p_wK22a_prompt", "p_wK22b_prompt", "p_wK22c_prompt")),
    PREGNANCY_WEEK_23(asList("p_wK23a", "p_wK23b", "p_wK23c"), asList("p_wK23a_prompt", "p_wK23b_prompt", "p_wK23c_prompt")),
    PREGNANCY_WEEK_24(asList("p_wK24a", "p_wK24b", "p_wK24c"), asList("p_wK24a_prompt", "p_wK24b_prompt", "p_wK24c_prompt")),
    PREGNANCY_WEEK_25(asList("p_wK25a", "p_wK25b", "p_wK25c"), asList("p_wK25a_prompt", "p_wK25b_prompt", "p_wK25c_prompt")),
    PREGNANCY_WEEK_26(asList("p_wK26a", "p_wK26b", "p_wK26c"), asList("p_wK26a_prompt", "p_wK26b_prompt", "p_wK26c_prompt")),
    PREGNANCY_WEEK_27(asList("p_wK27a", "p_wK27b", "p_wK27c"), asList("p_wK27a_prompt", "p_wK27b_prompt", "p_wK27c_prompt")),
    PREGNANCY_WEEK_28(asList("p_wK28a", "p_wK28b", "p_wK28c"), asList("p_wK28a_prompt", "p_wK28b_prompt", "p_wK28c_prompt")),
    PREGNANCY_WEEK_29(asList("p_wK29a", "p_wK29b", "p_wK29c"), asList("p_wK29a_prompt", "p_wK29b_prompt", "p_wK29c_prompt")),
    PREGNANCY_WEEK_30(asList("p_wK30a", "p_wK30b", "p_wK30c"), asList("p_wK30a_prompt", "p_wK30b_prompt", "p_wK30c_prompt")),
    PREGNANCY_WEEK_31(asList("p_wK31a", "p_wK31b", "p_wK31c"), asList("p_wK31a_prompt", "p_wK31b_prompt", "p_wK31c_prompt")),
    PREGNANCY_WEEK_32(asList("p_wK32a", "p_wK32b", "p_wK32c"), asList("p_wK32a_prompt", "p_wK32b_prompt", "p_wK32c_prompt")),
    PREGNANCY_WEEK_33(asList("p_wK33a", "p_wK33b", "p_wK33c"), asList("p_wK33a_prompt", "p_wK33b_prompt", "p_wK33c_prompt")),
    PREGNANCY_WEEK_34(asList("p_wK34a", "p_wK34b", "p_wK34c"), asList("p_wK34a_prompt", "p_wK34b_prompt", "p_wK34c_prompt")),
    PREGNANCY_WEEK_35(asList("p_wK35a", "p_wK35b", "p_wK35c"), asList("p_wK35a_prompt", "p_wK35b_prompt", "p_wK35c_prompt")),
    PREGNANCY_WEEK_36(asList("p_wK36a", "p_wK36b", "p_wK36c"), asList("p_wK36a_prompt", "p_wK36b_prompt", "p_wK36c_prompt")),
    PREGNANCY_WEEK_37(asList("p_wK37a", "p_wK37b", "p_wK37c"), asList("p_wK37a_prompt", "p_wK37b_prompt", "p_wK37c_prompt")),
    PREGNANCY_WEEK_38(asList("p_wK38a", "p_wK38b", "p_wK38c"), asList("p_wK38a_prompt", "p_wK38b_prompt", "p_wK38c_prompt")),
    PREGNANCY_WEEK_39(asList("p_wK39a", "p_wK39b", "p_wK39c"), asList("p_wK39a_prompt", "p_wK39b_prompt", "p_wK39c_prompt")),
    PREGNANCY_WEEK_40(asList("p_wK40a", "p_wK40b", "p_wK40c"), asList("p_wK40a_prompt", "p_wK40b_prompt", "p_wK40c_prompt"));


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


