/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 * @author William ALLADE
 *
 */
@Entity
public class ReglementFacture extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2517398394030798540L;

	//
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_regfacture")
	@SequenceGenerator(name = "gen_regfacture", sequenceName = "_seq_regfacture", allocationSize = 1)
	private Long id;
	//

	// Type de Paiement
	@ManyToOne
	private TypePaiement typePaiement;
	// Montant Reçu
	private Long montantRecu;
	// Montant payé
	private Long montantPayer;
	// Montant Rendu
	private Long montantRendu;
	// Description supplémentaire
	private String description;
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
	 * @return the typePaiement
	 */
	public TypePaiement getTypePaiement() {
		return typePaiement;
	}
	/**
	 * @param typePaiement the typePaiement to set
	 */
	public void setTypePaiement(TypePaiement typePaiement) {
		this.typePaiement = typePaiement;
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
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
