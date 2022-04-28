package com.alladeson.caurisit.security.core;


import com.alladeson.caurisit.security.entities.Account;

public class RegistrationPayload extends Account {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2401175721543264473L;
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
