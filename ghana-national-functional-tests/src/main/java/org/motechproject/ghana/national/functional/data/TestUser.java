package org.motechproject.ghana.national.functional.data;

public class TestUser {
    private String name;
    private String password;

    public static TestUser admin() {
        return new TestUser("admin", "P@ssw0rd");
    }

    public TestUser(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String name() {
        return name;
    }

    public String password() {
        return password;
    }
}
