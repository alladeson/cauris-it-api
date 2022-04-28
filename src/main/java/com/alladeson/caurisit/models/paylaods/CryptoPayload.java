/**
 * 
 */
package com.alladeson.caurisit.models.paylaods;

/**
 * @author TechDigita
 *
 */
public class CryptoPayload {
	private String originaleText;
	private String secretkey;
	private String encryptedString;
	/**
	 * @return the originaleText
	 */
	public String getOriginaleText() {
		return originaleText;
	}
	/**
	 * @param originaleText the originaleText to set
	 */
	public void setOriginaleText(String originaleText) {
		this.originaleText = originaleText;
	}
	/**
	 * @return the secretkey
	 */
	public String getSecretkey() {
		return secretkey;
	}
	/**
	 * @param secretkey the secretkey to set
	 */
	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}
	/**
	 * @return the encryptedString
	 */
	public String getEncryptedString() {
		return encryptedString;
	}
	/**
	 * @param encryptedString the encryptedString to set
	 */
	public void setEncryptedString(String encryptedString) {
		this.encryptedString = encryptedString;
	}
	
	
}
