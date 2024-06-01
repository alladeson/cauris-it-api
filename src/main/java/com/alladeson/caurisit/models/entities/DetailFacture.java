package com.alladeson.caurisit.models.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author William ALLADE
 *
 */
@Entity
public class DetailFacture extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7510041209697636112L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date date; // Date de validation de la ligne de facture
	// Le nom de l'article
	private String name;
	// La quantité de l'article
	private Double quantite;
	// L'unité de l'article
	private TypeData unite;
	// Le prix unitaire ht de l'article, sera récupéré de l'article
	private Double prixUht;
	// Le prix unitaire ttc de l'article
	private Double prixUnitaire;
	private Double montantHt;
	private Double montantTva;
//	private Double montantAib;
	private Double montantTtc;
	private boolean valid;

	// Gestion de remise ou modification du prix de l'article
	private boolean remise;
	// Entité Remise : en anglais remise = discount
	@OneToOne(cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
	private Remise discount;
	// Le taux de la remise
	@Transient
	private Integer taux;
	// Prix originale de l'article
	@Transient
	private Double originalPrice;
	// Description de la modification (ex. "Remise 50%")
	@Transient
	private String priceModification;

	// champ représente le montant HT de la taxe spécifique (TS) telle que reçu du
	// client REST,
	private Double taxeSpecifique;
	// Montant de la taxe spécifique TTC
	private Double tsTtc;
	// Pour le nom de la taxe spécifique, utile pour la personnalisation lors de l'émission de la facture de vente
	private String tsName;
	// Mise en rélation avec l'entité TaxeSpécifique, utile pour l'impression et
	// pour d'autres détails
	@OneToOne(cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
	private TaxeSpecifique ts;

	@ManyToOne
	private Taxe taxe;

	// Pour la récupérationd de l'id de la facture, aucune persistance à cet effet
	@Transient
	private Long taxeId;

	// Pour le type de facture, aucune persistance à cet effet
	@Transient
	private Long tfId;

	@ManyToOne
	private Article article;

	@ManyToOne
	@JsonIgnore
	private Facture facture;

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
	 * @return the unite
	 */
	public TypeData getUnite() {
		return unite;
	}

	/**
	 * @param unite the unite to set
	 */
	public void setUnite(TypeData unite) {
		this.unite = unite;
	}

	/**
	 * @return the prixUht
	 */
	public Double getPrixUht() {
		return prixUht;
	}

	/**
	 * @param prixUht the prixUht to set
	 */
	public void setPrixUht(Double prixUht) {
		this.prixUht = prixUht;
	}

	/**
	 * @return the prixUnitaire
	 */
	public Double getPrixUnitaire() {
		return prixUnitaire;
	}

	/**
	 * @param prixUnitaire the prixUnitaire to set
	 */
	public void setPrixUnitaire(Double prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
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

//	/**
//	 * @return the aib
//	 */
//	public Taxe getAib() {
//		return aib;
//	}
//
//	/**
//	 * @param aib the aib to set
//	 */
//	public void setAib(Taxe aib) {
//		this.aib = aib;
//	}

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
	 * @return the facture
	 */
	public Facture getFacture() {
		return facture;
	}

	/**
	 * @param facture the facture to set
	 */
	public void setFacture(Facture facture) {
		this.facture = facture;
	}

	/**
	 * @return the remise
	 */
	public boolean isRemise() {
		return remise;
	}

	/**
	 * @param remise the remise to set
	 */
	public void setRemise(boolean remise) {
		this.remise = remise;
	}

	/**
	 * @return the discount
	 */
	public Remise getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(Remise discount) {
		this.discount = discount;
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
	 * @return the tsTtc
	 */
	public Double getTsTtc() {
		return tsTtc;
	}

	/**
	 * @param tsTtc the tsTtc to set
	 */
	public void setTsTtc(Double tsTtc) {
		this.tsTtc = tsTtc;
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
	 * @return the ts
	 */
	public TaxeSpecifique getTs() {
		return ts;
	}

	/**
	 * @param ts the ts to set
	 */
	public void setTs(TaxeSpecifique ts) {
		this.ts = ts;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the taxeId
	 */
	public Long getTaxeId() {
		return taxeId;
	}

	/**
	 * @param taxeId the taxeId to set
	 */
	public void setTaxeId(Long taxeId) {
		this.taxeId = taxeId;
	}

	/**
	 * @return the tfId
	 */
	public Long getTfId() {
		return tfId;
	}

	/**
	 * @param tfId the tfId to set
	 */
	public void setTfId(Long tfId) {
		this.tfId = tfId;
	}
}
