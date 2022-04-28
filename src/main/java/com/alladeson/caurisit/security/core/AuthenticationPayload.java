package com.alladeson.caurisit.security.core;

public class AuthenticationPayload {
	/**
	 * User login that is either username, email, phone number or another user account ID.
	 */
	private String login;
	/**
	 * User credential.
	 */
	private String password;

	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
