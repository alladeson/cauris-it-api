package com.alladeson.caurisit.models.paylaods;

import com.alladeson.caurisit.models.entities.User;

public class SignupRequest extends User {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7876505114016013484L;
	
	private String password;
    private String confirmedPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }
}
