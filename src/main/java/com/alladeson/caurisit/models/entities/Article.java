/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author William ALLADE
 *
 */
@Table(uniqueConstraints = { 
		@UniqueConstraint(name = "UniqueDesignation", columnNames = { "designation" }),
		@UniqueConstraint(name = "UniqueReference", columnNames = { "reference" }),
})
@Entity
public class Article extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 122269624782441070L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	@Column(nullable = false)
	private String reference;
	@Column(nullable = false)
	private String designation;
	private Double prix;
	private Double stock;
	private Double stockSecurite;
	// Montant de la taxe spécifique (TS) par unité d'article
	private Double taxeSpecifique;
	// Le nom de la taxe spécifique
	private String tsName;
	
	@ManyToOne
	private CategorieArticle categorie;
	
	@ManyToOne
	private Taxe taxe;

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
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the prix
	 */
	public Double getPrix() {
		return prix;
	}

	/**
	 * @param prix the prix to set
	 */
	public void setPrix(Double prix) {
		this.prix = prix;
	}

	/**
	 * @return the stock
	 */
	public Double getStock() {
		return stock;
	}

	/**
	 * @param stock the stock to set
	 */
	public void setStock(Double stock) {
		this.stock = stock;
	}

	/**
	 * @return the stockSecurite
	 */
	public Double getStockSecurite() {
		return stockSecurite;
	}

	/**
	 * @param stockSecurite the stockSecurite to set
	 */
	public void setStockSecurite(Double stockSecurite) {
		this.stockSecurite = stockSecurite;
	}

	/**
	 * @return the taxeSpecifique
	 */
	public Double getTaxeSpecifique() {
		return taxeSpecifique;
	}

	/**
	 * @param taxeSpecifique the taxeSpecifique to set
	 */
	public void setTaxeSpecifique(Double taxeSpecifique) {
		this.taxeSpecifique = taxeSpecifique;
	}

	/**
	 * @return the tsName
	 */
	public String getTsName() {
		return tsName;
	}

	/**
	 * @param tsName the tsName to set
	 */
	public void setTsName(String tsName) {
		this.tsName = tsName;
	}

	/**
	 * @return the categorie
	 */
	public CategorieArticle getCategorie() {
		return categorie;
	}

	/**
	 * @param categorie the categorie to set
	 */
	public void setCategorie(CategorieArticle categorie) {
		this.categorie = categorie;
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
