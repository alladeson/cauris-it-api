/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author William ALLADE
 *
 */
@Entity
public class FactureFinalisationDgi extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3786954616225248627L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Les champs de l'objet de finalisation
	// 	Date et heure de la facture
	private String dateTime;
	// Contenu du code QR. Le champ est vide si la facture est annulée.
	private String qrCode;
	// Code MECeF/DGI.Le champ est vide si la facture est annulée
	private String codeMECeFDGI;
	// Compteurs
	private String counters;
	// NIM d’e-MCF
	private String nim;
	// Facultatif : code d’erreur en cas d’erreur
	private String errorCode;
	// Facultatif : description des erreurs en cas d’erreur
	private String errorDesc;
	
	// La facture ayant fait l'objet de finalisation
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
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @return the qrCode
	 */
	public String getQrCode() {
		return qrCode;
	}

	/**
	 * @param qrCode the qrCode to set
	 */
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	/**
	 * @return the codeMECeFDGI
	 */
	public String getCodeMECeFDGI() {
		return codeMECeFDGI;
	}

	/**
	 * @param codeMECeFDGI the codeMECeFDGI to set
	 */
	public void setCodeMECeFDGI(String codeMECeFDGI) {
		this.codeMECeFDGI = codeMECeFDGI;
	}

	/**
	 * @return the counters
	 */
	public String getCounters() {
		return counters;
	}

	/**
	 * @param counters the counters to set
	 */
	public void setCounters(String counters) {
		this.counters = counters;
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
