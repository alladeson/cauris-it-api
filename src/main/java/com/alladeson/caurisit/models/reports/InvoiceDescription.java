/**
 * 
 */
package com.alladeson.caurisit.models.reports;

/**
 * @author allad
 *
 */
public class InvoiceDescription {

	// L'objet de la facture
	private String objet;
	// Autres champs
	private String dossier;
	private String numeroBl;
	private String numeroPo;
	private Double montantTotal;
	private Double avance;
	private Double solde;

	/**
	 * @return the objet
	 */
	public String getObjet() {
		return objet;
	}

	/**
	 * @param objet the objet to set
	 */
	public void setObjet(String objet) {
		this.objet = objet;
	}

	/**
	 * @return the dossier
	 */
	public String getDossier() {
		return dossier;
	}

	/**
	 * @param dossier the dossier to set
	 */
	public void setDossier(String dossier) {
		this.dossier = dossier;
	}

	/**
	 * @return the numeroBl
	 */
	public String getNumeroBl() {
		return numeroBl;
	}

	/**
	 * @param numeroBl the numeroBl to set
	 */
	public void setNumeroBl(String numeroBl) {
		this.numeroBl = numeroBl;
	}

	/**
	 * @return the numeroPo
	 */
	public String getNumeroPo() {
		return numeroPo;
	}

	/**
	 * @param numeroPo the numeroPo to set
	 */
	public void setNumeroPo(String numeroPo) {
		this.numeroPo = numeroPo;
	}

	/**
	 * @return the montantTotal
	 */
	public Double getMontantTotal() {
		return montantTotal;
	}

	/**
	 * @param montantTotal the montantTotal to set
	 */
	public void setMontantTotal(Double montantTotal) {
		this.montantTotal = montantTotal;
	}

	/**
	 * @return the avance
	 */
	public Double getAvance() {
		return avance;
	}

	/**
	 * @param avance the avance to set
	 */
	public void setAvance(Double avance) {
		this.avance = avance;
	}

	/**
	 * @return the solde
	 */
	public Double getSolde() {
		return solde;
	}

	/**
	 * @param solde the solde to set
	 */
	public void setSolde(Double solde) {
		this.solde = solde;
	}
}
