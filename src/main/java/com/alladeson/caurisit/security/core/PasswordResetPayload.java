package com.alladeson.caurisit.security.core;

public class PasswordResetPayload {

    /**
     * User email.
     */
    private String email;

    private String defaultPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }
}
