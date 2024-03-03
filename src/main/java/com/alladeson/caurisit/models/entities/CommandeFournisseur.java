package com.alladeson.caurisit.models.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * @author William ALLADE
 *
 */
@Entity
public class CommandeFournisseur extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4613115298679522073L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// Le numero de la commande
	private String numero;
	// La date de crétation de la commande
	private Date dateCreation;
	// La date de livraison de la commande
	private Date dateLivraison;
	// La référence externe si elle existe
	private String referenceExterne;
	// La quantité totale des articles de la commande
	private Double qteTotal;
	// Le montant totale de la remise
	private Double montantRemise;
	// Le montant total hors taxe de la commande
	private Double montantHt;
	// Le montant total des tva
	private Double montantTva;
	// Le montant total TTC
	private Double montantTtc;
	// Notes sur la commande
	private String notes;
	// Référence de la facture du fournisseur : 
	// à renseigner lors de la validation de la commande
	private String referenceFactureFournisseur;
	// Pour la sauveagarde du nom de la commande :
	// Un fichier pdf à envoyer au fournisseur
	private String filename;
	// Pour la validation de la commande
	private boolean valid;

	@ManyToOne
	private Fournisseur fournisseur;

	@OneToMany(mappedBy = "commande", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private List<DetailCmdFournisseur> details;

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
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * @return the dateCreation
	 */
	public Date getDateCreation() {
		return dateCreation;
	}

	/**
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	/**
	 * @return the dateLivraison
	 */
	public Date getDateLivraison() {
		return dateLivraison;
	}

	/**
	 * @param dateLivraison the dateLivraison to set
	 */
	public void setDateLivraison(Date dateLivraison) {
		this.dateLivraison = dateLivraison;
	}

	/**
	 * @return the referenceExterne
	 */
	public String getReferenceExterne() {
		return referenceExterne;
	}

	/**
	 * @param referenceExterne the referenceExterne to set
	 */
	public void setReferenceExterne(String referenceExterne) {
		this.referenceExterne = referenceExterne;
	}

	/**
	 * @return the qteTotal
	 */
	public Double getQteTotal() {
		return qteTotal;
	}

	/**
	 * @param qteTotal the qteTotal to set
	 */
	public void setQteTotal(Double qteTotal) {
		this.qteTotal = qteTotal;
	}

	/**
	 * @return the montantRemise
	 */
	public Double getMontantRemise() {
		return montantRemise;
	}

	/**
	 * @param montantRemise the montantRemise to set
	 */
	public void setMontantRemise(Double montantRemise) {
		this.montantRemise = montantRemise;
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
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the referenceFactureFournisseur
	 */
	public String getReferenceFactureFournisseur() {
		return referenceFactureFournisseur;
	}

	/**
	 * @param referenceFactureFournisseur the referenceFactureFournisseur to set
	 */
	public void setReferenceFactureFournisseur(String referenceFactureFournisseur) {
		this.referenceFactureFournisseur = referenceFactureFournisseur;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
	 * @return the fournisseur
	 */
	public Fournisseur getFournisseur() {
		return fournisseur;
	}

	/**
	 * @param fournisseur the fournisseur to set
	 */
	public void setFournisseur(Fournisseur fournisseur) {
		this.fournisseur = fournisseur;
	}

	/**
	 * @return the details
	 */
	public List<DetailCmdFournisseur> getDetails() {
		if (details == null)
			details = new ArrayList<DetailCmdFournisseur>();
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(List<DetailCmdFournisseur> details) {
		this.details = details;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
