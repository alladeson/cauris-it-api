/**
 * 
 */
package com.alladeson.caurisit.models.reports;

/**
 * @author allad
 *
 */
public class ClientData {

	// Le nom du client (raisonSociale)
	private String name;
	// L'IFU du client
	private String ifu;
	// L'adresse du client
	private String address;
	// Le contact du client : combinaison du telephone et de l'email
	private String contact;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the ifu
	 */
	public String getIfu() {
		return ifu;
	}
	/**
	 * @param ifu the ifu to set
	 */
	public void setIfu(String ifu) {
		this.ifu = ifu;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}
	/**
	 * @param contact the contact to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}
	
}
