/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author William
 *
 */
@Entity
public class Parametre extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2201597649100566773L;

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
	// Le logo de la société
	private String logo;
	// Le numero de la machine e-mcef
	private String nim;

	// le jeton (token) de l'e-mecef
	@JsonIgnore
	@Column(length = 1000, nullable = false, unique = true)
	private String token;

	// Le type de system : Production ou Test
	private TypeSystem typeSystem;

	// Pour gérer le token, ce champs n'est pas enrgistrer dans la base
	// Il sert de tampon au token
	@Transient
	private String tokenTmp;

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
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * @return the token
	 */
//	@JsonIgnore
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
	 * @return the typeSystem
	 */
	public TypeSystem getTypeSystem() {
		return typeSystem;
	}

	/**
	 * @param typeSystem the typeSystem to set
	 */
	public void setTypeSystem(TypeSystem typeSystem) {
		this.typeSystem = typeSystem;
	}

	/**
	 * @return the nim
	 */
	public String getNim() {
		return nim;
	}

	/**
	 * @param nim the nim to set
	 */
	public void setNim(String nim) {
		this.nim = nim;
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
		if (this.telephone != null)
			contact += this.telephone;
		if (this.email != null)
			contact += "," + this.email;
		return contact;
	}

	/**
	 * @return the tokenTmp
	 */
	@JsonIgnore
	public String getTokenTmp() {
		return tokenTmp;
	}

	/**
	 * @param tokenTmp the tokenTmp to set
	 */
	public void setTokenTmp(String tokenTmp) {
		this.tokenTmp = tokenTmp;
	}
}
