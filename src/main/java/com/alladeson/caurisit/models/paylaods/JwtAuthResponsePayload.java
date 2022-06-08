/**
 * 
 */
package com.alladeson.caurisit.models.paylaods;

/**
 * @author allad
 *
 */
public class JwtAuthResponsePayload {
	private String token;
    private String tokenType;
    private String expiryToken;
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the tokenType
	 */
	public String getTokenType() {
		return tokenType;
	}
	/**
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	/**
	 * @return the expiryToken
	 */
	public String getExpiryToken() {
		return expiryToken;
	}
	/**
	 * @param expiryToken the expiryToken to set
	 */
	public void setExpiryToken(String expiryToken) {
		this.expiryToken = expiryToken;
	}
}
