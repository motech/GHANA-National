package org.ghana.national.tools.seed;

import org.ghana.national.domain.UserType;
import org.ghana.national.repository.AllUserTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTypeSeed extends Seed{

    @Autowired
    private AllUserTypes allUserTypes;

    @Override
    protected void load() {
        allUserTypes.add(new UserType(UserType.SUPER_ADMIN,"Super Administrator"));
        allUserTypes.add(new UserType(UserType.FACILITY_ADMIN,"Facility Administrator"));
        allUserTypes.add(new UserType(UserType.CALL_CENTER_ADMIN,"CallCentre Administrator"));
        allUserTypes.add(new UserType(UserType.HEALTH_CARE_ADMIN,"HealthCare Administrator"));
        allUserTypes.add(new UserType(UserType.COMMUNITY_HEALTH_OPERATOR,"Community Health Operator"));
        allUserTypes.add(new UserType(UserType.COMMUNITY_HEALTH_NURSE,"Community Heath Nurse"));
        allUserTypes.add(new UserType(UserType.COMMUNITY_HEALTH_VOLUNTEER,"Community Heath Volunteer"));
        allUserTypes.add(new UserType(UserType.HEALTH_PROMOTION_OFFICER,"Health Promotion Officer"));
        allUserTypes.add(new UserType(UserType.FIELD_AGENT,"Field Agent"));
        allUserTypes.add(new UserType(UserType.MOBILE_MIDWIFE,"Mobile Midwife"));
    }
}
