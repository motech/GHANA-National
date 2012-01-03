package org.motechproject.functional.data;

public class OpenMRSTestUser {
    private String name;
    private String password;

    public OpenMRSTestUser(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String name() {
        return name;
    }

    public String password() {
        return password;
    }

    public static OpenMRSTestUser admin() {
        return new OpenMRSTestUser("admin", "P@ssw0rd");
    }
}
