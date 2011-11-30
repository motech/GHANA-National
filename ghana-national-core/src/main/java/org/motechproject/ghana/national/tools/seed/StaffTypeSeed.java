package org.motechproject.ghana.national.tools.seed;

import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.repository.AllStaffTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaffTypeSeed extends Seed {

    @Autowired
    private AllStaffTypes allStaffTypes;

    @Override
    protected void load() {
        allStaffTypes.add(new StaffType(StaffType.Role.SUPER_ADMIN.key(), "Super Administrator"));
        allStaffTypes.add(new StaffType(StaffType.Role.FACILITY_ADMIN.key(), "Facility Administrator"));
        allStaffTypes.add(new StaffType(StaffType.Role.CALL_CENTER_ADMIN.key(), "CallCentre Administrator"));
        allStaffTypes.add(new StaffType(StaffType.Role.HEALTH_CARE_ADMIN.key(), "HealthCare Administrator"));
        allStaffTypes.add(new StaffType(StaffType.Role.HEALTH_EXTENSION_WORKER.key(), "Health Extension Worker"));
        allStaffTypes.add(new StaffType(StaffType.Role.COMMUNITY_HEALTH_OPERATOR.key(), "Community Health Operator"));
        allStaffTypes.add(new StaffType(StaffType.Role.COMMUNITY_HEALTH_NURSE.key(), "Community Heath Nurse"));
        allStaffTypes.add(new StaffType(StaffType.Role.COMMUNITY_HEALTH_VOLUNTEER.key(), "Community Heath Volunteer"));
        allStaffTypes.add(new StaffType(StaffType.Role.HEALTH_PROMOTION_OFFICER.key(), "Health Promotion Officer"));
        allStaffTypes.add(new StaffType(StaffType.Role.FIELD_AGENT.key(), "Field Agent"));
        allStaffTypes.add(new StaffType(StaffType.Role.MOBILE_MIDWIFE.key(), "Mobile Midwife"));
    }
}
