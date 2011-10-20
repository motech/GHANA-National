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
        allUserTypes.add(new UserType("Facility Admin","Facility Administrator"));
        allUserTypes.add(new UserType("Call Centre Admin","Call Centre Administrator"));
        allUserTypes.add(new UserType("CHO","Community Health Operator"));
        allUserTypes.add(new UserType("CHN","Community Heath Nurse"));
        allUserTypes.add(new UserType("CHV","Community Heath Volunteer"));
        allUserTypes.add(new UserType("HPO","Health Promotion Officer"));
        allUserTypes.add(new UserType("FA","Field Agent"));
        allUserTypes.add(new UserType("MMA","Mobile Midwife"));
    }
}
