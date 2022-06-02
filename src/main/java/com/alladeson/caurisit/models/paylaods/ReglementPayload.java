/**
 * 
 */
package com.alladeson.caurisit.models.paylaods;

import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TypeFactureEnum;
import com.alladeson.caurisit.models.entities.TypePaiementEnum;

/**
 * @author allad
 *
 */
public class ReglementPayload {

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
	
}
