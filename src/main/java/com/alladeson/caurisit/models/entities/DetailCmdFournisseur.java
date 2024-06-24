package com.alladeson.caurisit.models.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author William ALLADE
 *
 */
@Entity
public class DetailCmdFournisseur extends BaseEntity {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4895466564591129199L;
	//
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_detail_cmdf")
	@SequenceGenerator(name = "gen_detail_cmdf", sequenceName = "_seq_detail_cmdf", allocationSize = 1)
	private Long id;
	//
	// La référence de l'article
	private String reference;
	// Le nom ou désignation de l'article
	private String name;
	// La quantité de l'article
	private Double quantite;
	// Le prix unitaire HT de l'article,
	private Double prixUht;
	// Le prix unitaire ttc de l'article
	private Double prixUnitaire;
	// Le montant total hors taxe
	private Double montantHt;
	// Le montant de la tva
	private Double montantTva;
	// Le montant total tout taxe comprise
	private Double montantTtc;
	// Gérer la validation de la ligne de commande
	private boolean valid;
	// Date de validation de la ligne de commande
	private Date date; 
	// Pour vérifier si ce détail n'est pas pour la livraison
	private boolean expedition;

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

	@ManyToOne
	private Taxe taxe;

	// Pour la récupérationd de l'id de la facture, aucune persistance à cet effet
	@Transient
	private Long taxeId;

	@ManyToOne
	private Article article;

	@ManyToOne
	@JsonIgnore
	private CommandeFournisseur commande;

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
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
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
	 * @return the expedition
	 */
	public boolean isExpedition() {
		return expedition;
	}

	/**
	 * @param expedition the expedition to set
	 */
	public void setExpedition(boolean expedition) {
		this.expedition = expedition;
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
	 * @return the commande
	 */
	public CommandeFournisseur getCommande() {
		return commande;
	}

	/**
	 * @param commande the commande to set
	 */
	public void setCommande(CommandeFournisseur commande) {
		this.commande = commande;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
