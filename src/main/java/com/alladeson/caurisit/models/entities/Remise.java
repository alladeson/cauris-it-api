/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author William ALLADE
 *
 */
@Entity
public class Remise extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5810883768037890917L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// Le pourcentage de la remise
	private Integer taux;
	// Prix originale de l'article
	private Double originalPrice;
	// Description de la modification (ex. "Remise 50%")
	private String priceModification;
	// Pour stocker le montant de la remise : utile pour les calcules
	private Double montant;
	

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
	 * @return the taux
	 */
	public Integer getTaux() {
		return taux;
	}

	/**
	 * @param taux the taux to set
	 */
	public void setTaux(Integer taux) {
		this.taux = taux;
	}

	/**
	 * @return the originalPrice
	 */
	public Double getOriginalPrice() {
		return originalPrice;
	}

	/**
	 * @param originalPrice the originalPrice to set
	 */
	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}

	/**
	 * @return the priceModification
	 */
	public String getPriceModification() {
		return priceModification;
	}

	/**
	 * @param priceModification the priceModification to set
	 */
	public void setPriceModification(String priceModification) {
		this.priceModification = priceModification;
	}

	/**
	 * @return the montant
	 */
	public Double getMontant() {
		return montant;
	}

	/**
	 * @param montant the montant to set
	 */
	public void setMontant(Double montant) {
		this.montant = montant;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
