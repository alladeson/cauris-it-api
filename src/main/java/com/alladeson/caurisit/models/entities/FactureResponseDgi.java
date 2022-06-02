/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author allad
 *
 */
@Entity
public class FactureResponseDgi extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5427992458719359740L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// Date de validation de la facture
	private Date date;
	// Les champs de la reponse
	// Identification de la transaction. Cette valeur est utilisée en demande de
	// finalisation.
	private String uid;
	// Valeur du groupe de taxation A (en %)
	private Long ta;
	// Valeur du groupe de taxation B (en %)
	private Long tb;
	// Valeur du groupe de taxation C (en %)
	private Long tc;
	// Valeur du groupe de taxation D (en %)
	private Long td;
	// Montant total pour le groupe A
	private Long taa;
	// Montant total pour le groupe B
	private Long tab;
	// Montant total pour le groupe C
	private Long tac;
	// Montant total pour le groupe D
	private Long tad;
	// Montant total pour le groupe E
	private Long tae;
	// Montant total pour le groupe F
	private Long taf;
	// Montant HT pour le groupe B
	private Long hab;
	// Montant HT pour le groupe D
	private Long had;
	//  Montant TVA pour le groupe B
	private Long vab;
	// Montant TVA pour le groupe D
	private Long vad;
	// Montant de l’AIB
	private Long aib;
	// Montant de l’impôt spécifique
	private Long ts;
	// Montant total sur la facture
	private Long total;
	// Facultatif : code d’erreur en cas d’erreur. Uid est nul dans ce cas
	private String errorCode;
	// Facultatif : description des erreurs en cas d’erreur. Uid est nul dans ce cas
	private String errorDesc;
	
	@OneToOne
	private Facture facture;

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
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the ta
	 */
	public Long getTa() {
		return ta;
	}

	/**
	 * @param ta the ta to set
	 */
	public void setTa(Long ta) {
		this.ta = ta;
	}

	/**
	 * @return the tb
	 */
	public Long getTb() {
		return tb;
	}

	/**
	 * @param tb the tb to set
	 */
	public void setTb(Long tb) {
		this.tb = tb;
	}

	/**
	 * @return the tc
	 */
	public Long getTc() {
		return tc;
	}

	/**
	 * @param tc the tc to set
	 */
	public void setTc(Long tc) {
		this.tc = tc;
	}

	/**
	 * @return the td
	 */
	public Long getTd() {
		return td;
	}

	/**
	 * @param td the td to set
	 */
	public void setTd(Long td) {
		this.td = td;
	}

	/**
	 * @return the taa
	 */
	public Long getTaa() {
		return taa;
	}

	/**
	 * @param taa the taa to set
	 */
	public void setTaa(Long taa) {
		this.taa = taa;
	}

	/**
	 * @return the tab
	 */
	public Long getTab() {
		return tab;
	}

	/**
	 * @param tab the tab to set
	 */
	public void setTab(Long tab) {
		this.tab = tab;
	}

	/**
	 * @return the tac
	 */
	public Long getTac() {
		return tac;
	}

	/**
	 * @param tac the tac to set
	 */
	public void setTac(Long tac) {
		this.tac = tac;
	}

	/**
	 * @return the tad
	 */
	public Long getTad() {
		return tad;
	}

	/**
	 * @param tad the tad to set
	 */
	public void setTad(Long tad) {
		this.tad = tad;
	}

	/**
	 * @return the tae
	 */
	public Long getTae() {
		return tae;
	}

	/**
	 * @param tae the tae to set
	 */
	public void setTae(Long tae) {
		this.tae = tae;
	}

	/**
	 * @return the taf
	 */
	public Long getTaf() {
		return taf;
	}

	/**
	 * @param taf the taf to set
	 */
	public void setTaf(Long taf) {
		this.taf = taf;
	}

	/**
	 * @return the hab
	 */
	public Long getHab() {
		return hab;
	}

	/**
	 * @param hab the hab to set
	 */
	public void setHab(Long hab) {
		this.hab = hab;
	}

	/**
	 * @return the had
	 */
	public Long getHad() {
		return had;
	}

	/**
	 * @param had the had to set
	 */
	public void setHad(Long had) {
		this.had = had;
	}

	/**
	 * @return the vab
	 */
	public Long getVab() {
		return vab;
	}

	/**
	 * @param vab the vab to set
	 */
	public void setVab(Long vab) {
		this.vab = vab;
	}

	/**
	 * @return the vad
	 */
	public Long getVad() {
		return vad;
	}

	/**
	 * @param vad the vad to set
	 */
	public void setVad(Long vad) {
		this.vad = vad;
	}

	/**
	 * @return the aib
	 */
	public Long getAib() {
		return aib;
	}

	/**
	 * @param aib the aib to set
	 */
	public void setAib(Long aib) {
		this.aib = aib;
	}

	/**
	 * @return the ts
	 */
	public Long getTs() {
		return ts;
	}

	/**
	 * @param ts the ts to set
	 */
	public void setTs(Long ts) {
		this.ts = ts;
	}

	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}

	/**
	 * @param errorDesc the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	/**
	 * @return the facture
	 */
	public Facture getFacture() {
		return facture;
	}

	/**
	 * @param facture the facture to set
	 */
	public void setFacture(Facture facture) {
		this.facture = facture;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
