/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.ManyToOne;

/**
 * @author William
 *
 */
@Entity
public class Client extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3179188902873955865L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String ifu;
	private String name;
	private String telephone;
	private String email;
	private String address;
	private String raisonSociale;
	private String ville;
	private String pays;
	private String rcm;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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
	 * @return the raisonSociale
	 */
	public String getRaisonSociale() {
		return raisonSociale;
	}

	/**
	 * @param raisonSociale the raisonSociale to set
	 */
	public void setRaisonSociale(String raisonSociale) {
		this.raisonSociale = raisonSociale;
	}

	/**
	 * @return the ville
	 */
	public String getVille() {
		return ville;
	}

	/**
	 * @param ville the ville to set
	 */
	public void setVille(String ville) {
		this.ville = ville;
	}

	/**
	 * @return the pays
	 */
	public String getPays() {
		return pays;
	}

	/**
	 * @param pays the pays to set
	 */
	public void setPays(String pays) {
		this.pays = pays;
	}

	/**
	 * @return the rcm
	 */
	public String getRcm() {
		return rcm;
	}

	/**
	 * @param rcm the rcm to set
	 */
	public void setRcm(String rcm) {
		this.rcm = rcm;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Retourne le contact qui lest la combinaison du telephone et de l'email
	 * 
	 * @return contact
	 */
	public String getContact() {
		String contact = "";
		if (this.telephone !== "" )
			contact += this.telephone;
		if (this.email !== "") {
			if (this.telephone !== "")
				contact += "," + this.email;
			else
				contact += this.email;
		}
		return contact;
	}
}
