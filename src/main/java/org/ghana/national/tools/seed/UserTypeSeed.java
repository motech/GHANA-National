package org.ghana.national.tools.seed;

import org.ghana.national.domain.UserType;
import org.ghana.national.repository.AllUserTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTypeSeed extends Seed {

    @Autowired
    private AllUserTypes allUserTypes;

    @Override
    protected void load() {
        allUserTypes.add(new UserType(UserType.Role.SUPER_ADMIN.key(), "Super Administrator"));
        allUserTypes.add(new UserType(UserType.Role.FACILITY_ADMIN.key(), "Facility Administrator"));
        allUserTypes.add(new UserType(UserType.Role.CALL_CENTER_ADMIN.key(), "CallCentre Administrator"));
        allUserTypes.add(new UserType(UserType.Role.HEALTH_CARE_ADMIN.key(), "HealthCare Administrator"));
        allUserTypes.add(new UserType(UserType.Role.COMMUNITY_HEALTH_OPERATOR.key(), "Community Health Operator"));
        allUserTypes.add(new UserType(UserType.Role.COMMUNITY_HEALTH_NURSE.key(), "Community Heath Nurse"));
        allUserTypes.add(new UserType(UserType.Role.COMMUNITY_HEALTH_VOLUNTEER.key(), "Community Heath Volunteer"));
        allUserTypes.add(new UserType(UserType.Role.HEALTH_PROMOTION_OFFICER.key(), "Health Promotion Officer"));
        allUserTypes.add(new UserType(UserType.Role.FIELD_AGENT.key(), "Field Agent"));
        allUserTypes.add(new UserType(UserType.Role.MOBILE_MIDWIFE.key(), "Mobile Midwife"));
    }
}
