package com.alladeson.caurisit.security.core;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;

public class PasswordPayload {

    private String oldValue;
    @NotBlank
    private String newValue;
    private String confirmedValue;

    public String getOld() {
        return oldValue;
    }

    public void setOld(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNew() {
        return newValue;
    }

    public void setNew(String newValue) {
        this.newValue = newValue;
    }

    public String getConfirmed() {
        return confirmedValue;
    }

    public void setConfirmed(String confirmedValue) {
        this.confirmedValue = confirmedValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("old", oldValue)
                .append("new", newValue)
                .append("confirmed", confirmedValue)
                .toString();
    }
}
