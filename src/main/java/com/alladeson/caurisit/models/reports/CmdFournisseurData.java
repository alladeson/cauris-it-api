/**
 * 
 */
package com.alladeson.caurisit.models.reports;

/**
 * @author allad
 *
 */
public class CmdFournisseurData {

	// Les informations de la société
	private String steIfu;
	private String steName;
	private String steTelephone;
	private String steEmail;
	private String steAddress;
	private String steVille;
	private String steAddressContact;
	private String steRccm;
	private String steLogo;

	// Le numéro de la commande
	private String numero;
	// La date de création de la commande
	private String DateCreation;
	// La date de livraison de la commande
	private String DateLivraison;
	// La référence externe de la commande
	private String referenceExterne;
	// Les notes de la commande
	private String notes;
	// La quantité total de la commande
	private Double qteTotal;
	// Le montant total des remises
	private Double montantRemise;
	// Le montant total HT
	private Double montantHt;
	// Le montant total TVA
	private Double montantTva;
	// Le montant total TTC
	private Double montantTtc;
	
	// Les données du fournisseur
	private String fournisseurName;
	private String fournisseurAddress;
	private String fournisseurVille;
	private String fournisseurAddressContact;
	private String fournisseurTelephone;
	
	
	/**
	 * @return the steIfu
	 */
	public String getSteIfu() {
		return steIfu;
	}
	/**
	 * @return the steName
	 */
	public String getSteName() {
		return steName;
	}
	/**
	 * @return the steTelephone
	 */
	public String getSteTelephone() {
		return steTelephone;
	}
	/**
	 * @return the steEmail
	 */
	public String getSteEmail() {
		return steEmail;
	}
	/**
	 * @return the steAddress
	 */
	public String getSteAddress() {
		return steAddress;
	}
	/**
	 * @return the steVille
	 */
	public String getSteVille() {
		return steVille;
	}
	/**
	 * @return the steAddressContact
	 */
	public String getSteAddressContact() {
		return steAddressContact;
	}
	/**
	 * @return the steRccm
	 */
	public String getSteRccm() {
		return steRccm;
	}
	/**
	 * @return the steLogo
	 */
	public String getSteLogo() {
		return steLogo;
	}
	/**
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}
	/**
	 * @return the dateCreation
	 */
	public String getDateCreation() {
		return DateCreation;
	}
	/**
	 * @return the dateLivraison
	 */
	public String getDateLivraison() {
		return DateLivraison;
	}
	/**
	 * @return the referenceExterne
	 */
	public String getReferenceExterne() {
		return referenceExterne;
	}
	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}
	/**
	 * @return the qteTotal
	 */
	public Double getQteTotal() {
		return qteTotal;
	}
	/**
	 * @return the montantRemise
	 */
	public Double getMontantRemise() {
		return montantRemise;
	}
	/**
	 * @return the montantHt
	 */
	public Double getMontantHt() {
		return montantHt;
	}
	/**
	 * @return the montantTva
	 */
	public Double getMontantTva() {
		return montantTva;
	}
	/**
	 * @return the montantTtc
	 */
	public Double getMontantTtc() {
		return montantTtc;
	}
	/**
	 * @return the fournisseurName
	 */
	public String getFournisseurName() {
		return fournisseurName;
	}
	/**
	 * @return the fournisseurAddress
	 */
	public String getFournisseurAddress() {
		return fournisseurAddress;
	}
	/**
	 * @return the fournisseurVille
	 */
	public String getFournisseurVille() {
		return fournisseurVille;
	}
	/**
	 * @return the fournisseurAddressContact
	 */
	public String getFournisseurAddressContact() {
		return fournisseurAddressContact;
	}
	/**
	 * @return the fournisseurTelephone
	 */
	public String getFournisseurTelephone() {
		return fournisseurTelephone;
	}
	/**
	 * @param steIfu the steIfu to set
	 */
	public void setSteIfu(String steIfu) {
		this.steIfu = steIfu;
	}
	/**
	 * @param steName the steName to set
	 */
	public void setSteName(String steName) {
		this.steName = steName;
	}
	/**
	 * @param steTelephone the steTelephone to set
	 */
	public void setSteTelephone(String steTelephone) {
		this.steTelephone = steTelephone;
	}
	/**
	 * @param steEmail the steEmail to set
	 */
	public void setSteEmail(String steEmail) {
		this.steEmail = steEmail;
	}
	/**
	 * @param steAddress the steAddress to set
	 */
	public void setSteAddress(String steAddress) {
		this.steAddress = steAddress;
	}
	/**
	 * @param steVille the steVille to set
	 */
	public void setSteVille(String steVille) {
		this.steVille = steVille;
	}
	/**
	 * @param steAddressContact the steAddressContact to set
	 */
	public void setSteAddressContact(String steAddressContact) {
		this.steAddressContact = steAddressContact;
	}
	/**
	 * @param steRccm the steRccm to set
	 */
	public void setSteRccm(String steRccm) {
		this.steRccm = steRccm;
	}
	/**
	 * @param steLogo the steLogo to set
	 */
	public void setSteLogo(String steLogo) {
		this.steLogo = steLogo;
	}
	/**
	 * @param numero the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}
	/**
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(String dateCreation) {
		DateCreation = dateCreation;
	}
	/**
	 * @param dateLivraison the dateLivraison to set
	 */
	public void setDateLivraison(String dateLivraison) {
		DateLivraison = dateLivraison;
	}
	/**
	 * @param referenceExterne the referenceExterne to set
	 */
	public void setReferenceExterne(String referenceExterne) {
		this.referenceExterne = referenceExterne;
	}
	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	/**
	 * @param qteTotal the qteTotal to set
	 */
	public void setQteTotal(Double qteTotal) {
		this.qteTotal = qteTotal;
	}
	/**
	 * @param montantRemise the montantRemise to set
	 */
	public void setMontantRemise(Double montantRemise) {
		this.montantRemise = montantRemise;
	}
	/**
	 * @param montantHt the montantHt to set
	 */
	public void setMontantHt(Double montantHt) {
		this.montantHt = montantHt;
	}
	/**
	 * @param montantTva the montantTva to set
	 */
	public void setMontantTva(Double montantTva) {
		this.montantTva = montantTva;
	}
	/**
	 * @param montantTtc the montantTtc to set
	 */
	public void setMontantTtc(Double montantTtc) {
		this.montantTtc = montantTtc;
	}
	/**
	 * @param fournisseurName the fournisseurName to set
	 */
	public void setFournisseurName(String fournisseurName) {
		this.fournisseurName = fournisseurName;
	}
	/**
	 * @param fournisseurAddress the fournisseurAddress to set
	 */
	public void setFournisseurAddress(String fournisseurAddress) {
		this.fournisseurAddress = fournisseurAddress;
	}
	/**
	 * @param fournisseurVille the fournisseurVille to set
	 */
	public void setFournisseurVille(String fournisseurVille) {
		this.fournisseurVille = fournisseurVille;
	}
	/**
	 * @param fournisseurAddressContact the fournisseurAddressContact to set
	 */
	public void setFournisseurAddressContact(String fournisseurAddressContact) {
		this.fournisseurAddressContact = fournisseurAddressContact;
	}
	/**
	 * @param fournisseurTelephone the fournisseurTelephone to set
	 */
	public void setFournisseurTelephone(String fournisseurTelephone) {
		this.fournisseurTelephone = fournisseurTelephone;
	}
	
}
