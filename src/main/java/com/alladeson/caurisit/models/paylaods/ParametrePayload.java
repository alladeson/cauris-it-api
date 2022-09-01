/**
 * 
 */
package com.alladeson.caurisit.models.paylaods;


import com.alladeson.caurisit.models.entities.TypeSystem;

/**
 * @author allad
 *
 */
public class ParametrePayload {

	private String ifu;
	private String name;
	private String telephone;
	private String email;
	private String 	address;
	private String raisonSociale;
	private String ville;
	private String pays;
	private String rcm;
	//Le numero de la machine e-mcef
	private String nim;
	// le jeton (token) de l'e-mecef
	private String token;
	// Le type de system : Production ou Test
	private TypeSystem typeSystem;
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
}
