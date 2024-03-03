/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
		@UniqueConstraint(name = "UniqueNim", columnNames = { "nim" }),
		@UniqueConstraint(name = "UniqueToken", columnNames = { "token" }),
		@UniqueConstraint(name = "UniqueSerialKey", columnNames = { "serialKey" })
})
@Entity
public class Parametre extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2201597649100566773L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
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
	// Le logo de la société
	private String logo;
	// Le numero de la machine e-mcef
	@Column(nullable = false)
	private String nim;

	// le jeton (token) de l'e-mecef
	@JsonIgnore
	@Column(length = 1000, nullable = false)
	private String token;

	// Le type de system : Production ou Test
	private TypeSystem typeSystem;
	// La date d'expiration de l'emcef
	@Column(nullable = false)
	private Date expiration;

	// Pour gérer le token, ce champs n'est pas enrgistrer dans la base
	// Il sert de tampon au token
	@Transient
	private String tokenTmp;
	
	// Pour l'enregistrement de la clé d'activation
	@Column(nullable = false)
	private String serialKey;
	// Enregistrement de la date de configuration
	private Date activationDate;
	// Pour le fichier du rapport de la configuration
	private String configReport;
	// Pour la format d'impression de la facture
	@Column(nullable = false)
	private TypeData formatFacture;

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
	 * @return the expiration
	 */
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialVersionUID() {
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
		if (this.email != null) {
			if (this.telephone != null)
				contact += ", " + this.email;
			else
				contact += this.email;

		}
		return contact;
	}

	/**
	 * @return the tokenTmp
	 */
	public String getTokenTmp() {
		return tokenTmp;
	}

	/**
	 * @param tokenTmp the tokenTmp to set
	 */
	public void setTokenTmp(String tokenTmp) {
		this.tokenTmp = tokenTmp;
	}

	/**
	 * @return the serialKey
	 */
	public String getSerialKey() {
		return serialKey;
	}

	/**
	 * @param serialKey the serialKey to set
	 */
	public void setSerialKey(String serialKey) {
		this.serialKey = serialKey;
	}

	/**
	 * @return the activationDate
	 */
	public Date getActivationDate() {
		return activationDate;
	}

	/**
	 * @param activationDate the activationDate to set
	 */
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	/**
	 * @return the configReport
	 */
	public String getConfigReport() {
		return configReport;
	}

	/**
	 * @param configReport the configReport to set
	 */
	public void setConfigReport(String configReport) {
		this.configReport = configReport;
	}

	/**
	 * @return the formatFacture
	 */
	public TypeData getFormatFacture() {
		return formatFacture;
	}

	/**
	 * @param formatFacture the formatFacture to set
	 */
	public void setFormatFacture(TypeData formatFacture) {
		this.formatFacture = formatFacture;
	}
}
