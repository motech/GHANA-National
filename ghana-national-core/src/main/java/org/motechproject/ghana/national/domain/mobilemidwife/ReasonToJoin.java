package org.motechproject.ghana.national.domain.mobilemidwife;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public enum ReasonToJoin{
    CURRENTLY_PREGNANT("Currently pregnant"),
    RECENTLY_DELIVERED("Recently delivery"),
    FAMILY_FRIEND_PREGNANT("Family/friend pregnant"),
    FAMILY_FRIEND_DELIVERED("Family/friend recently delivered"),
    PLANNING_PREGNANCY_INFO("Thinking of getting pregnant"),
    KNOW_MORE_PREGNANCY_CHILDBIRTH("Want to know more about pregnancy and child birth"),
    WORK_WITH_WOMEN_NEWBORNS("Work with pregnant women or newborns");

    private String displayName;

    ReasonToJoin(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReasonToJoin value(String input) {
       return isNotEmpty(input) ? ReasonToJoin.valueOf(input) : null;
    }

    public String getValue(){
        return name();
    }

}
