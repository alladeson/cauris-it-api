/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import java.util.Date;

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
public class Approvisionnement extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 844136332994530176L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date date; // Date de l'approvisionnement
	private Long quantite;
	private Double prixHt;
	private Double montantHt;
	private Double montantTva;
	private Double montantTtc;
	private boolean valid;
	
	@ManyToOne
	private Taxe taxe;
	
	@ManyToOne
	private Article article;

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
	 * @return the quantite
	 */
	public Long getQuantite() {
		return quantite;
	}

	/**
	 * @param quantite the quantite to set
	 */
	public void setQuantite(Long quantite) {
		this.quantite = quantite;
	}

	/**
	 * @return the prixHt
	 */
	public Double getPrixHt() {
		return prixHt;
	}

	/**
	 * @param prixHt the prixHt to set
	 */
	public void setPrixHt(Double prixHt) {
		this.prixHt = prixHt;
	}

	/**
	 * @return the montantHt
	 */
	public Double getMontantHt() {
		return montantHt;
	}

	/**
	 * @param montantHt the montantHt to set
	 */
	public void setMontantHt(Double montantHt) {
		this.montantHt = montantHt;
	}

	/**
	 * @return the montantTva
	 */
	public Double getMontantTva() {
		return montantTva;
	}

	/**
	 * @param montantTva the montantTva to set
	 */
	public void setMontantTva(Double montantTva) {
		this.montantTva = montantTva;
	}

	/**
	 * @return the montantTtc
	 */
	public Double getMontantTtc() {
		return montantTtc;
	}

	/**
	 * @param montantTtc the montantTtc to set
	 */
	public void setMontantTtc(Double montantTtc) {
		this.montantTtc = montantTtc;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
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
	 * @return the article
	 */
	public Article getArticle() {
		return article;
	}

	/**
	 * @param article the article to set
	 */
	public void setArticle(Article article) {
		this.article = article;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
