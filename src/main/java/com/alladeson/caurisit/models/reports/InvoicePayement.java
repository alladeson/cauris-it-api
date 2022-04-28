/**
 * 
 */
package com.alladeson.caurisit.models.reports;

/**
 * @author allad
 *
 */
public class InvoicePayement {

	// Le tpe de payement de la facture
	private String type_payement;
	// Le montant reçu du client
	private Long montant;
	// Le montant payé
	private Long payer;
	// Le montant rendu au client
	private Long rendu;
	/**
	 * @return the type_payement
	 */
	public String getType_payement() {
		return type_payement;
	}
	/**
	 * @param type_payement the type_payement to set
	 */
	public void setType_payement(String type_payement) {
		this.type_payement = type_payement;
	}
	/**
	 * @return the montant
	 */
	public Long getMontant() {
		return montant;
	}
	/**
	 * @param montant the montant to set
	 */
	public void setMontant(Long montant) {
		this.montant = montant;
	}
	/**
	 * @return the payer
	 */
	public Long getPayer() {
		return payer;
	}
	/**
	 * @param payer the payer to set
	 */
	public void setPayer(Long payer) {
		this.payer = payer;
	}
	/**
	 * @return the rendu
	 */
	public Long getRendu() {
		return rendu;
	}
	/**
	 * @param rendu the rendu to set
	 */
	public void setRendu(Long rendu) {
		this.rendu = rendu;
	}
	
}
