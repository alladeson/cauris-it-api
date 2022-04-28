package com.alladeson.caurisit.models.reports;

public class InvoiceDetailData {

	// numéro d'ordre
	private int numero;
	// La désignation de l'article
	private String name;
	// Le prix unitaire ttc de l'article
	private Long prix_u;
	// La quantité de l'article
	private Double qte;
	// Le montant T.T.C pour l'article
	private String montant_ttc;
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
	
}
