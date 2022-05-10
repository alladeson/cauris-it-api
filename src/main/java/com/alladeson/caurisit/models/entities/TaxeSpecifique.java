/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author allad
 *
 */
@Entity
public class TaxeSpecifique extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6297984190197404167L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// Le nom de la taxe spécifique
	private String name;
	// La taxe spécifique HT par unité de l'article
	private Double tsUnitaire;
	// La taxe spécifique TTC par unité de l'article
	private Double tsUnitaireTtc;
	// La quantité de l'article
	private Double quantite;
	// La TS total hors taxe telle que reçu de l'utilisateur
	// tsTotalHt = quantite * tsUnitaire
	private Double tsTotalHt;
	// La taxe spécifique TTC pour toute la quantité
	// tsTotal = tsTotalHt * (1+taux); le taux issu de la taxe
	private Double tsTotal;

	// Le taux appliqué
	@ManyToOne
	private Taxe taxe;

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
	 * @return the tsUnitaire
	 */
	public Double getTsUnitaire() {
		return tsUnitaire;
	}

	/**
	 * @param tsUnitaire the tsUnitaire to set
	 */
	public void setTsUnitaire(Double tsUnitaire) {
		this.tsUnitaire = tsUnitaire;
	}

	/**
	 * @return the tsUnitaireTtc
	 */
	public Double getTsUnitaireTtc() {
		return tsUnitaireTtc;
	}

	/**
	 * @param tsUnitaireTtc the tsUnitaireTtc to set
	 */
	public void setTsUnitaireTtc(Double tsUnitaireTtc) {
		this.tsUnitaireTtc = tsUnitaireTtc;
	}

	/**
	 * @return the quantite
	 */
	public Double getQuantite() {
		return quantite;
	}

	/**
	 * @param quantite the quantite to set
	 */
	public void setQuantite(Double quantite) {
		this.quantite = quantite;
	}

	/**
	 * @return the tsTotalHt
	 */
	public Double getTsTotalHt() {
		return tsTotalHt;
	}

	/**
	 * @param tsTotalHt the tsTotalHt to set
	 */
	public void setTsTotalHt(Double tsTotalHt) {
		this.tsTotalHt = tsTotalHt;
	}

	/**
	 * @return the tsTotal
	 */
	public Double getTsTotal() {
		return tsTotal;
	}

	/**
	 * @param tsTotal the tsTotal to set
	 */
	public void setTsTotal(Double tsTotal) {
		this.tsTotal = tsTotal;
	}

	/**
	 * @return the taxe
	 */
	public Taxe getTaxe() {
		return taxe;
	}

	/**
	 * @param taxe the taxe to set
	 */
	public void setTaxe(Taxe taxe) {
		this.taxe = taxe;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
