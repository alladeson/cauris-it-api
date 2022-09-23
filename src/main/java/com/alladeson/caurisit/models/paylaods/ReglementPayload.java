/**
 * 
 */
package com.alladeson.caurisit.models.paylaods;

/**
 * @author allad
 *
 */
public class ReglementPayload {

	/* Début anciens champs */
	// aib de la facture
	private Long aibId;
	// Type de Paiement
	private Long typePaiementId;
	// Montant Reçu
	private Long montantRecu;
	// Montant payé
	private Long montantPayer;
	// Montant Rendu
	private Long montantRendu;
	// Description supplémentaire
	private String description;
	/* Fin anciens champs */

	// Les champ de la description de la facture
	private String objet;
	private String dossier;
	private String numeroBl;
	private String numeroPo;
	private Double montantTotal;
	private Double avance;
	private Double solde;

	/**
	 * @return the aibId
	 */
	public Long getAibId() {
		return aibId;
	}

	/**
	 * @param aibId the aibId to set
	 */
	public void setAibId(Long aibId) {
		this.aibId = aibId;
	}

	/**
	 * @return the typePaiementId
	 */
	public Long getTypePaiementId() {
		return typePaiementId;
	}

	/**
	 * @param typePaiementId the typePaiementId to set
	 */
	public void setTypePaiementId(Long typePaiementId) {
		this.typePaiementId = typePaiementId;
	}

	/**
	 * @return the montantRecu
	 */
	public Long getMontantRecu() {
		return montantRecu;
	}

	/**
	 * @param montantRecu the montantRecu to set
	 */
	public void setMontantRecu(Long montantRecu) {
		this.montantRecu = montantRecu;
	}

	/**
	 * @return the montantPayer
	 */
	public Long getMontantPayer() {
		return montantPayer;
	}

	/**
	 * @param montantPayer the montantPayer to set
	 */
	public void setMontantPayer(Long montantPayer) {
		this.montantPayer = montantPayer;
	}

	/**
	 * @return the montantRendu
	 */
	public Long getMontantRendu() {
		return montantRendu;
	}

	/**
	 * @param montantRendu the montantRendu to set
	 */
	public void setMontantRendu(Long montantRendu) {
		this.montantRendu = montantRendu;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

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
