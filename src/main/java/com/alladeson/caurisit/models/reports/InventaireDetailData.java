package com.alladeson.caurisit.models.reports;

public class InventaireDetailData {

	// la référence de l'article
	private String reference;
	// La désignation de l'article
	private String name;
	// La quantité de l'article
	private Double qte;
	
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
	 * @return the qte
	 */
	public Double getQte() {
		return qte;
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
	 * @param qte the qte to set
	 */
	public void setQte(Double qte) {
		this.qte = qte;
	}
}
