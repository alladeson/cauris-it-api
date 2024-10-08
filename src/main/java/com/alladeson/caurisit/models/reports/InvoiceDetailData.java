package com.alladeson.caurisit.models.reports;

public class InvoiceDetailData {

	// numéro d'ordre
	private int numero;
	// La désignation de l'article
	private String name;
	// La taxe (en abréviation) de l'article
	private String taxe;
	// Le prix unitaire ttc de l'article
	private Long prix_u;
	// La quantité de l'article
	private Double qte;
	// L'unité de l'article
	private String unite;
	// Le montant T.T.C pour l'article
	private String montant_ttc;
	// Le taux de la remise
	private String remise;

	/**
	 * @return the numero
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * @param numéro the numero to set
	 */
	public void setNumero(int numero) {
		this.numero = numero;
	}

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
	 * @return the taxe
	 */
	public String getTaxe() {
		return taxe;
	}

	/**
	 * @param taxe the taxe to set
	 */
	public void setTaxe(String taxe) {
		this.taxe = taxe;
	}

	/**
	 * @return the prix_u
	 */
	public Long getPrix_u() {
		return prix_u;
	}

	/**
	 * @param prix_u the prix_u to set
	 */
	public void setPrix_u(Long prix_u) {
		this.prix_u = prix_u;
	}

	/**
	 * @return the qte
	 */
	public Double getQte() {
		return qte;
	}

	/**
	 * @param qte the qte to set
	 */
	public void setQte(Double qte) {
		this.qte = qte;
	}

	/**
	 * @return the unite
	 */
	public String getUnite() {
		return unite;
	}

	/**
	 * @param unite the unite to set
	 */
	public void setUnite(String unite) {
		this.unite = unite;
	}

	/**
	 * @return the montant_ttc
	 */
	public String getMontant_ttc() {
		return montant_ttc;
	}

	/**
	 * @param montant_ttc the montant_ttc to set
	 */
	public void setMontant_ttc(String montant_ttc) {
		this.montant_ttc = montant_ttc;
	}

	/**
	 * @return the remise
	 */
	public String getRemise() {
		return remise;
	}

	/**
	 * @param remise the remise to set
	 */
	public void setRemise(String remise) {
		this.remise = remise;
	}

}
