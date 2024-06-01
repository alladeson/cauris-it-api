/**
 * 
 */
package com.alladeson.caurisit.models.reports;

/**
 * @author allad
 *
 */
public class BilanPeriodiqueData {

	// La date de debut de la période
	private String dateDebut;
	// La date de fin de la période
	private String dateFin;
	// Le nombre de facture de vente de la période
	private Integer nbFv;
	// Le nombre de facture d'avoir dans la période
	private Integer nbFa;
	// Le nombre de facture de vente n'ayant pas fait objet de facture d'avoir dans la période
	private Integer nbFvRecap;
	// Le montant total des factures de vente dans la période
	private Long totalFv;
	// Le montant total des factures d'avoir dans la période
	private Long totalFa;
	// Le montant total des factures de vente n'ayant pas fait objet de facture d'avoir dans la période
	private Long totalFvRecap;
	// Le taux d'imposition
	private double tauxImpot;
	// Montant impot
	private Long montantImpot;
	
	// Les informations de la société
	private String ste_ifu;
	private String ste_name;
	private String ste_telephone;
	private String ste_email;
	private String ste_address;
	private String ste_raisonSociale;
	private String ste_ville;
	private String ste_pays;
	private String ste_rccm;
	private String ste_logo;
	private String ste_contact;
	// Le numero de la machine e-mcef
	private String emcef_nim;

	/**
	 * @return the dateDebut
	 */
	public String getDateDebut() {
		return dateDebut;
	}

	/**
	 * @param dateDebut the dateDebut to set
	 */
	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}

	/**
	 * @return the dateFin
	 */
	public String getDateFin() {
		return dateFin;
	}

	/**
	 * @param dateFin the dateFin to set
	 */
	public void setDateFin(String dateFin) {
		this.dateFin = dateFin;
	}

	/**
	 * @return the nbFv
	 */
	public Integer getNbFv() {
		return nbFv;
	}

	/**
	 * @param nbFv the nbFv to set
	 */
	public void setNbFv(Integer nbFv) {
		this.nbFv = nbFv;
	}

	/**
	 * @return the nbFa
	 */
	public Integer getNbFa() {
		return nbFa;
	}

	/**
	 * @param nbFa the nbFa to set
	 */
	public void setNbFa(Integer nbFa) {
		this.nbFa = nbFa;
	}

	/**
	 * @return the nbFvRecap
	 */
	public Integer getNbFvRecap() {
		return nbFvRecap;
	}

	/**
	 * @param nbFvRecap the nbFvRecap to set
	 */
	public void setNbFvRecap(Integer nbFvRecap) {
		this.nbFvRecap = nbFvRecap;
	}

	/**
	 * @return the totalFv
	 */
	public Long getTotalFv() {
		return totalFv;
	}

	/**
	 * @param totalFv the totalFv to set
	 */
	public void setTotalFv(Long totalFv) {
		this.totalFv = totalFv;
	}

	/**
	 * @return the totalFa
	 */
	public Long getTotalFa() {
		return totalFa;
	}

	/**
	 * @param totalFa the totalFa to set
	 */
	public void setTotalFa(Long totalFa) {
		this.totalFa = totalFa;
	}

	/**
	 * @return the totalFvRecap
	 */
	public Long getTotalFvRecap() {
		return totalFvRecap;
	}

	/**
	 * @param totalFvRecap the totalFvRecap to set
	 */
	public void setTotalFvRecap(Long totalFvRecap) {
		this.totalFvRecap = totalFvRecap;
	}

	/**
	 * @return the tauxImpot
	 */
	public double getTauxImpot() {
		return tauxImpot;
	}

	/**
	 * @return the montantImpot
	 */
	public Long getMontantImpot() {
		return montantImpot;
	}

	/**
	 * @param tauxImpot the tauxImpot to set
	 */
	public void setTauxImpot(double tauxImpot) {
		this.tauxImpot = tauxImpot;
	}

	/**
	 * @param montantImpot the montantImpot to set
	 */
	public void setMontantImpot(Long montantImpot) {
		this.montantImpot = montantImpot;
	}

	/**
	 * @return the ste_ifu
	 */
	public String getSte_ifu() {
		return ste_ifu;
	}

	/**
	 * @param ste_ifu the ste_ifu to set
	 */
	public void setSte_ifu(String ste_ifu) {
		this.ste_ifu = ste_ifu;
	}

	/**
	 * @return the ste_name
	 */
	public String getSte_name() {
		return ste_name;
	}

	/**
	 * @param ste_name the ste_name to set
	 */
	public void setSte_name(String ste_name) {
		this.ste_name = ste_name;
	}

	/**
	 * @return the ste_telephone
	 */
	public String getSte_telephone() {
		return ste_telephone;
	}

	/**
	 * @param ste_telephone the ste_telephone to set
	 */
	public void setSte_telephone(String ste_telephone) {
		this.ste_telephone = ste_telephone;
	}

	/**
	 * @return the ste_email
	 */
	public String getSte_email() {
		return ste_email;
	}

	/**
	 * @param ste_email the ste_email to set
	 */
	public void setSte_email(String ste_email) {
		this.ste_email = ste_email;
	}

	/**
	 * @return the ste_address
	 */
	public String getSte_address() {
		return ste_address;
	}

	/**
	 * @param ste_address the ste_address to set
	 */
	public void setSte_address(String ste_address) {
		this.ste_address = ste_address;
	}

	/**
	 * @return the ste_raisonSociale
	 */
	public String getSte_raisonSociale() {
		return ste_raisonSociale;
	}

	/**
	 * @param ste_raisonSociale the ste_raisonSociale to set
	 */
	public void setSte_raisonSociale(String ste_raisonSociale) {
		this.ste_raisonSociale = ste_raisonSociale;
	}

	/**
	 * @return the ste_ville
	 */
	public String getSte_ville() {
		return ste_ville;
	}

	/**
	 * @param ste_ville the ste_ville to set
	 */
	public void setSte_ville(String ste_ville) {
		this.ste_ville = ste_ville;
	}

	/**
	 * @return the ste_pays
	 */
	public String getSte_pays() {
		return ste_pays;
	}

	/**
	 * @param ste_pays the ste_pays to set
	 */
	public void setSte_pays(String ste_pays) {
		this.ste_pays = ste_pays;
	}

	/**
	 * @return the ste_rccm
	 */
	public String getSte_rccm() {
		return ste_rccm;
	}

	/**
	 * @param ste_rccm the ste_rccm to set
	 */
	public void setSte_rccm(String ste_rccm) {
		this.ste_rccm = ste_rccm;
	}

	/**
	 * @return the ste_logo
	 */
	public String getSte_logo() {
		return ste_logo;
	}

	/**
	 * @param ste_logo the ste_logo to set
	 */
	public void setSte_logo(String ste_logo) {
		this.ste_logo = ste_logo;
	}

	/**
	 * @return the ste_contact
	 */
	public String getSte_contact() {
		return ste_contact;
	}

	/**
	 * @param ste_contact the ste_contact to set
	 */
	public void setSte_contact(String ste_contact) {
		this.ste_contact = ste_contact;
	}

	/**
	 * @return the emcef_nim
	 */
	public String getEmcef_nim() {
		return emcef_nim;
	}

	/**
	 * @param emcef_nim the emcef_nim to set
	 */
	public void setEmcef_nim(String emcef_nim) {
		this.emcef_nim = emcef_nim;
	}

}
