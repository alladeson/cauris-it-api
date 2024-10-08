/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


/**
 * @author William ALLADE
 *
 */
@Table(uniqueConstraints = { 
		@UniqueConstraint(name = "UniqueIfu", columnNames = { "ifu" }),
		@UniqueConstraint(name = "UniqueName", columnNames = { "name" }),
		@UniqueConstraint(name = "UniqueRcm", columnNames = { "rcm" }),
		@UniqueConstraint(name = "UniquePhone", columnNames = { "telephone" }),
		@UniqueConstraint(name = "UniqueEmail", columnNames = { "email" }),
})
@Entity
public class Fournisseur extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 124807912428741457L;

	//
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_fournisseur")
	@SequenceGenerator(name = "gen_fournisseur", sequenceName = "_seq_fournisseur", allocationSize = 1)
	private Long id;
	//

	// @Column(nullable = false)
	private String ifu;
	@Column(nullable = false)
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
	 * Retourne le contact qui lest la combinaison du telephone et de l'email
	 * 
	 * @return contact
	 */
	public String getContact() {
		String contact = "";
		if (this.telephone != null)
			contact += this.telephone;
		if (this.email != null) {
			if (this.telephone != null)
				contact += ", " + this.email;
			else
				contact += this.email;
		}
		return contact;
	}
	
	/**
	 * Retourne l'adresse qui est la combinaison de l'adress et de la ville
	 * 
	 * @return the complete address
	 */
	public String getAddressContact() {
		String address = "";
		if (this.address != null)
			address += this.address;
		if (this.ville != null) {
			if (this.address != null)
				address += ", " + this.ville;
			else
				address += this.ville;

		}
		return address;
	}
}
