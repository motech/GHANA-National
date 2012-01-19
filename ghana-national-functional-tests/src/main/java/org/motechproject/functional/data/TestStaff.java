package org.motechproject.functional.data;

public class TestStaff {
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String emailId;
    private STAFF_ROLE role;
    private String motechId;

    public static TestStaff with(String firstName){
        TestStaff testStaff = new TestStaff();
        testStaff.firstName = firstName;
        testStaff.middleName = "Middle Name";
        testStaff.lastName = "Last Name";
        testStaff.phoneNumber = "0123456789";
        testStaff.emailId = "email@ghana.com";
        testStaff.role = STAFF_ROLE.COMMUNITY_HEALTH_NURSE;
        return testStaff;
    }

    public String firstName() {
        return firstName;
    }

    public String middleName() {
        return middleName;
    }

    public String lastName() {
        return lastName;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String emailId() {
        return emailId;
    }

    public String motechId() {
        return motechId;
    }

    public STAFF_ROLE role() {
        return role;
    }

    public TestStaff motechId(String motechId) {
        this.motechId = motechId;
        return this;
    }

    public static enum STAFF_ROLE{
        SUPER_ADMIN("Super Admin (Super Administrator)", "Super Admin"),
        FACILITY_ADMIN("Facility Admin (Facility Administrator)", "Facility Admin"),
        CALL_CENTER_ADMIN("CallCenter Admin (Call Centre Administrator)", "CallCenter Admin"),
        HEALTH_CARE_ADMIN("HealthCare Admin (Health Care Administrator)", "HealthCare Admin"),
        HEALTH_EXT_WORKER("HEW (Health Extension Worker)", "HEW"),
        COMMUNITY_HEALTH_WORKER("CHO (Community Health Officer)", "CHO"),
        COMMUNITY_HEALTH_NURSE("CHN (Community Health Nurse)", "CHN"),
        COMMUNITY_HEALTH_VOLUNTEER("CHV (Community Health Volunteer)", "CHV"),
        COMMUNITY_HEALTH_OFFICER("HPO (Health Promotion Officer)", "HPO"),
        FIELD_AGENT("FA (Motech Field Agent)", "FA"),
        MOBILE_MIDWIFE_AGENT("MMA (Mobile Midwife Agent)", "MMA");

        private String role;
        private String shortName;

        STAFF_ROLE(String role, String shortName) {
            this.role = role;
            this.shortName = shortName;
        }

        public String getRole() {
            return role;
        }

        public String getShortName() {
            return shortName;
        }
    }

}
