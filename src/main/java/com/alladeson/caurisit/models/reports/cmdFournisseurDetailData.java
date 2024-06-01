package com.alladeson.caurisit.models.reports;

public class cmdFournisseurDetailData {

	// Pour stocker l'id du detail de la commande
	private Long id;
	// La référence de l'article
	private String reference;
	// La désignation de l'article
	private String name;	
	// Le prix unitaire HT de l'article
	private Double prixUht;
	// La quantité de l'article
	private Double quantite;
	// Le montant HT de l'article
	private Double montantHt;
	// Le taux de la remise
	private Integer remise;
	// Le taux de la taxe TVA
	private Integer taxe;
	
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
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the prixUht
	 */
	public Double getPrixUht() {
		return prixUht;
	}
	/**
	 * @return the quantite
	 */
	public Double getQuantite() {
		return quantite;
	}
	/**
	 * @return the montantHt
	 */
	public Double getMontantHt() {
		return montantHt;
	}
	/**
	 * @return the remise
	 */
	public Integer getRemise() {
		return remise;
	}
	/**
	 * @return the taxe
	 */
	public Integer getTaxe() {
		return taxe;
	}
	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param prixUht the prixUht to set
	 */
	public void setPrixUht(Double prixUht) {
		this.prixUht = prixUht;
	}
	/**
	 * @param quantite the quantite to set
	 */
	public void setQuantite(Double quantite) {
		this.quantite = quantite;
	}
	/**
	 * @param montantHt the montantHt to set
	 */
	public void setMontantHt(Double montantHt) {
		this.montantHt = montantHt;
	}
	/**
	 * @param remise the remise to set
	 */
	public void setRemise(Integer remise) {
		this.remise = remise;
	}
	/**
	 * @param taxe the taxe to set
	 */
	public void setTaxe(Integer taxe) {
		this.taxe = taxe;
	}

}
